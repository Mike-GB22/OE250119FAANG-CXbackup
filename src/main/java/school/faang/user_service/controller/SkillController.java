package school.faang.user_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.skill.SkillCandidateDto;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.service.SkillService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SkillController {
    private static final String NOT_VALID_SKILL_MSG_EXCEPTION_ID_IS_NULL = "Skill's id is null";
    private static final String NOT_VALID_SKILL_MSG_EXCEPTION_TITLE_IS_BLANK = "Skill's title is blank";

    private final SkillService skillService;
    private final SkillMapper skillMapper;

    public SkillDto create(SkillDto skillDto) {
        validateSkill(skillDto);
        Skill skill = skillService.create(skillMapper.toEntity(skillDto));

        return skillMapper.toDto(skill);
    }

    public List<SkillDto> getUserSkills(long userId) {
        return skillService.getUserSkills(userId)
                .stream()
                .map(skillMapper::toDto).toList();
    }

    public List<SkillCandidateDto> getOfferedSkills(long userId) {
        return skillService.getOfferedSkills(userId).entrySet().stream()
                .map(entry ->
                        new SkillCandidateDto(
                                skillMapper.toDto(entry.getKey()), entry.getValue()))
                .toList();
    }

    public SkillDto acquireSkillFromOffers(long skillId, long userId) {
        Skill skill = skillService.acquireSkillFromOffers(skillId, userId);
        return skillMapper.toDto(skill);
    }

    private void validateSkill(SkillDto skill) {
        if (skill.getId() == null) {
            throw new DataValidationException(NOT_VALID_SKILL_MSG_EXCEPTION_ID_IS_NULL);
        } else if (skill.getTitle().isBlank()) {
            throw new DataValidationException(NOT_VALID_SKILL_MSG_EXCEPTION_TITLE_IS_BLANK);
        }
    }
}
