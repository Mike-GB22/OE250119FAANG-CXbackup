package school.faang.user_service.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.recommendation.Recommendation;
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
    private final RecommendationRepository recommendationRepository;
    private final SkillOfferRepository skillOfferRepository;
    private final SkillRepository skillRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
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
            throw new DataValidationException("Skills are not presented in system " +
                    "or recommendation was given less than 6 months ago ");
        }
    }

    public RecommendationDto update(RecommendationDto recommendationDto) {

        if (LocalDateTime.now().minusMonths(6).isAfter(getTimeOfLastRecommendation(recommendationDto))
                && recommendationsArePresentedInSystem(recommendationDto.getSkillOffers())) {
            recommendationRepository.update(
                    recommendationDto.getAuthorId(),
                    recommendationDto.getReceiverId(),
                    recommendationDto.getContent());

            List<SkillOfferDto> skillOfferDtos = recommendationDto.getSkillOffers();
            saveSkillOffers(skillOfferDtos, recommendationDto.getId());
            addSkillAndAddGuarantor(recommendationDto, recommendationDto.getId());

            Optional<Recommendation> recommendationFromDataBase = recommendationRepository.findById(recommendationDto.getId());
            return recommendationFromDataBase.map(recommendationMapper::toDto).orElse(null);
        } else {
            throw new DataValidationException(SKILLS_PRESENCE_ERROR + "or" + RECOMMENDATION_FREQUENCY_ERROR);
        }
    }

    /**
     * - Removes skill offers from "skill_offer" table to keep the table consistent
     * - Removes recommendation from "recommendation" table
     * - Does not remove guarntor from skills - doesn't delete lines from "user_skill_guarantee" table.(To brainstorm)
     *
     * @param id - id of recommendation to remove
     */
    public void delete(long id) {
        skillOfferRepository.deleteAllByRecommendationId(id);
        recommendationRepository.deleteById(id);
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

    private boolean recommendationsArePresentedInSystem(List<SkillOfferDto> skillOfferDtos) {
        for (SkillOfferDto skill : skillOfferDtos) {
            if (!skillRepository.existsByTitle(skill.getTitle())) {
                return false;
            }
        }
        return true;
    }

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
                } else { // добавление нового скила юзеру и прикрепление гаранта
                    skillRepository.assignSkillToUser(skill.getSkillId(), recommendationDto.getReceiverId());
                    addGuarantorToSkill(recommendationDto, skill);
                }
            }
        }
    }

    /**
     * Returns time of last recommendation from DB created by recommendationDto.authorId to recommendationDto.receiverId
     *
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
        for (SkillOfferDto skillOfferDto : skillOfferDtos) {
            skillOfferRepository.create(skillOfferDto.getSkillId(), newRecommendationId);
        }
    }


    private void addGuarantorToSkill(RecommendationDto recommendationDto, SkillOfferDto skillOfferDto) {
        userSkillGuaranteeRepository.create(recommendationDto.getId(),
                skillOfferDto.getSkillId(),
                recommendationDto.getAuthorId());
    }
}
