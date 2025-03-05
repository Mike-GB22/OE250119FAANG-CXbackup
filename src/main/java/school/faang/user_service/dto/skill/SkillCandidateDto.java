package school.faang.user_service.dto.skill;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SkillCandidateDto {
    SkillDto skill;
    long offersAmount;
}
