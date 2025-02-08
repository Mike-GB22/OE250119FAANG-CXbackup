package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface ProjectMapper {

    @Mapping(target = "parentProjectId", source = "parentProject.id")
    ProjectDto toProjectDto(Project project);

    List<ProjectDto> toProjectsList(List<Project> projectsList);

}
