package school.faang.user_service.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.skill.SkillDto;
import school.faang.user_service.entity.Skill;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface SkillMapper {
    @InheritInverseConfiguration
    SkillDto toDto(Skill skill);

    @Mapping(target="users", ignore = true)
    @Mapping(target="guarantees", ignore = true)
    @Mapping(target="events", ignore = true)
    @Mapping(target="goals", ignore = true)
    @Mapping(target="createdAt", ignore = true)
    @Mapping(target="updatedAt", ignore = true)
    Skill toEntity(SkillDto skillDto);
}
