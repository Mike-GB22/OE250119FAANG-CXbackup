package school.faang.user_service.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

import static org.junit.jupiter.api.Assertions.*;

class SkillMapperTest {
    private static final String TITLE = "-title-";
    private static final Long ID = 12345L;

    private Skill skill;
    private SkillDto skillDto;
    private SkillMapper skillMapper;

    @BeforeEach
    void setUp() {
        skillMapper = new SkillMapperImpl();
        skill = new Skill();
        skill.setTitle(TITLE);
        skill.setId(ID);

        skillDto = new SkillDto(ID, TITLE);
    }

    @Test
    void toDto() {
        SkillDto testDto = skillMapper.toDto(skill);
        assertEquals(skillDto, testDto);
    }

    @Test
    void toEntity() {
        Skill testEntity = skillMapper.toEntity(skillDto);
        assertEquals(skill.getId(), testEntity.getId());
        assertEquals(skill.getTitle(), testEntity.getTitle());
    }
}