package school.faang.user_service.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Getter
@Service
@AllArgsConstructor
public class RecommendationService {
    private static final String SKILLS_PRESENCE_ERROR = "Skills are not presented in system ";
    private static final String RECOMMENDATION_FREQUENCY_ERROR = "recommendation was given less than 6 months ago";
    private static final String RECOMMENDATION_NOT_FOUND = "recommendation is not found in DB";
    @Autowired
    private final RecommendationRepository recommendationRepository;
    @Autowired
    private final SkillOfferRepository skillOfferRepository;
    @Autowired
    private final SkillRepository skillRepository;
    @Autowired
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    @Autowired
    private final RecommendationMapper recommendationMapper;
    private static final int PAGE_NUMBER = 0;
    private static final int PAGE_SIZE = 10;


    public RecommendationDto create(RecommendationDto recommendationDto) {

        Long newRecommendationId;
        if (LocalDateTime.now().minusMonths(6).isAfter(getTimeOfLastRecommendation(recommendationDto))
                && recommendationsArePresentedInSystem(recommendationDto.getSkillOffers())) {

            newRecommendationId = saveRecommendation(recommendationDto);
            List<SkillOfferDto> skillOfferDtos = recommendationDto.getSkillOffers();

            saveSkillOffers(skillOfferDtos, newRecommendationId);
            addSkillAndAddGuarantor(recommendationDto, newRecommendationId);

            Optional<Recommendation> recommendationFromDataBase = recommendationRepository.findById(newRecommendationId);
            return recommendationFromDataBase.map(recommendationMapper::toDto).orElse(null);
        } else {
            throw new DataValidationException(SKILLS_PRESENCE_ERROR + RECOMMENDATION_FREQUENCY_ERROR);
        }
    }

    /**
     * - updates only recommendation content of "recommendation" table
     * - Does not update guarantors of skills - doesn't delete lines from "user_skill_guarantee" table.(To brainstorm)
     * - Does not update skill offers from "skill_offer" table to keep the table consistent
     * - Does not update skills from "user_skill" table to keep the table consistent
     * @param recommendationDto - recommendation to update
     */
    public RecommendationDto update(RecommendationDto recommendationDto) {

        if (LocalDateTime.now().minusMonths(6).isAfter(getTimeOfLastRecommendation(recommendationDto))
                && recommendationsArePresentedInSystem(recommendationDto.getSkillOffers())) {
            recommendationRepository.update(
                    recommendationDto.getAuthorId(),
                    recommendationDto.getReceiverId(),
                    recommendationDto.getContent());

            List<SkillOfferDto> skillOfferDtos = recommendationDto.getSkillOffers();
            updateRecommendation(recommendationDto);

            Optional<Recommendation> recommendationFromDataBase = recommendationRepository.findById(recommendationDto.getId());
            return recommendationFromDataBase.map(recommendationMapper::toDto).orElse(null);
        } else {
            throw new DataValidationException(SKILLS_PRESENCE_ERROR + "or" + RECOMMENDATION_FREQUENCY_ERROR);
        }
    }

    /**
     * - Removes recommendation from "recommendation" table
     * - Removes skill offers from "skill_offer" table due to hibernate annotation
     * - Does not remove guarantor from skills - doesn't delete lines from "user_skill_guarantee" table.(To brainstorm)
     * - Does not remove skills from "user_skill" table to keep the table consistent
     * @param id - id of recommendation to remove
     */
    @Transactional
    public void delete(long id) {
        Optional<Recommendation> optional = recommendationRepository.findById(id);
        RecommendationDto recommendationDto = optional.map(recommendationMapper::toDto).orElse(null);
        if(recommendationDto != null) {
            recommendationRepository.deleteById(id);
        } else {
            throw new DataValidationException(RECOMMENDATION_NOT_FOUND);
        }
    }

    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        Page<Recommendation> recommendationPage = recommendationRepository.findAllByReceiverId(
                receiverId,
                PageRequest.of(PAGE_NUMBER, PAGE_SIZE));
        return recommendationPage.stream()
                .map(recommendationMapper::toDto)
                .toList();
    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        Page<Recommendation> recommendationPage = recommendationRepository.findAllByAuthorId(
                authorId,
                PageRequest.of(PAGE_NUMBER, PAGE_SIZE));
        return recommendationPage.stream()
                .map(recommendationMapper::toDto)
                .toList();
    }

    private boolean  recommendationsArePresentedInSystem(@NonNull List<SkillOfferDto> skillOfferDtos) {
        for (SkillOfferDto skill : skillOfferDtos) {
            if (!skillRepository.existsByTitle(skill.getTitle())) {
                return false;
            }
        }
        return true;
    }

    @Transactional
    private void addSkillAndAddGuarantor(RecommendationDto recommendationDto, long newRecommendationId) {
        List<SkillOfferDto> skillOfferDtos = recommendationDto.getSkillOffers();
        if (!skillOfferDtos.isEmpty()) {
            List<Skill> userOldSkills = skillRepository.findAllByUserId(recommendationDto.getReceiverId());
            List<Skill> allSkillsGuaranteedToUserByGuarantee = userSkillGuaranteeRepository
                    .findAllSkillsGuaranteedToUserByGuarantee(
                            recommendationDto.getReceiverId(),
                            recommendationDto.getAuthorId());
            for (SkillOfferDto skill : skillOfferDtos) {
                if (userOldSkills.contains(skill)) {
                    if (!allSkillsGuaranteedToUserByGuarantee.contains(skill)) {
                        addGuarantorToSkill(recommendationDto, skill);
                    }
                } else {
                    skillRepository.assignSkillToUser(skill.getSkillId(), recommendationDto.getReceiverId());
                    addGuarantorToSkill(recommendationDto, skill);
                }
            }
        }
    }

    /**
     * Returns time of last recommendation from DB created by recommendationDto.authorId to recommendationDto.receiverId
     * @param recommendationDto - an object of recommendation received from API
     * @return - date of last recommendation if recommendation exists in Database
     * - Jan 01 0 (BC) if there is no recommendation
     */
    private LocalDateTime getTimeOfLastRecommendation(RecommendationDto recommendationDto) {
        Optional<Recommendation> recommendation = recommendationRepository.findFirstByAuthorIdAndReceiverIdOrderByCreatedAtDesc(
                recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId());
        return recommendation.map(Recommendation::getUpdatedAt).orElse(LocalDateTime.of(0, 1, 1, 0, 0));
    }

    private Long saveRecommendation(RecommendationDto recommendationDto) {
        return recommendationRepository.create(recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(),
                recommendationDto.getContent());
    }

    private void saveSkillOffers(List<SkillOfferDto> skillOfferDtos, Long newRecommendationId) {
        if(skillOfferDtos!=null) {
            for (SkillOfferDto skillOfferDto : skillOfferDtos) {
                skillOfferRepository.create(skillOfferDto.getSkillId(), newRecommendationId);
            }
        }
    }

    private void updateRecommendation(RecommendationDto recommendationDto) {
        recommendationRepository.update(recommendationDto.getAuthorId(),
                recommendationDto.getReceiverId(), recommendationDto.getContent());
    }

    @Transactional
    private void addGuarantorToSkill(RecommendationDto recommendationDto, SkillOfferDto skillOfferDto) {
        userSkillGuaranteeRepository.create(
                recommendationDto.getReceiverId(),
                skillOfferDto.getSkillId(),
                recommendationDto.getAuthorId());
    }
}
