package faang.school.projectservice.mapper.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MomentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userIds", ignore = true)
    @Mapping(target = "imageId", ignore = true)
    @Mapping(target = "resource", expression = "java(mapResourceIdsToResources(momentDto))")
    @Mapping(target = "projects", expression = "java(mapProjectIdsToProjects(momentDto))")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Moment toEntity(MomentDto momentDto);

    @Mapping(target = "projectIds", expression = "java(mapProjectsToIds(moment.getProjects()))")
    @Mapping(target = "resourceIds", expression = "java(mapResourceToIds(moment.getResource()))")
    MomentDto toDto(Moment moment);


    @Mapping(target = "projects", ignore = true)
    @Mapping(target = "userIds", ignore = true)
    @Mapping(target = "imageId", ignore = true)
    @Mapping(target = "resource", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    void updateMomentFromDto(MomentDto dto, @MappingTarget Moment moment);

    default List<Long> mapProjectsToIds(List<Project> projects) {
        return   projects != null
                ? projects.stream().map(Project::getId).collect(Collectors.toList())
                : List.of();
    }

    default List<Long> mapResourceToIds(List<Resource> resources) {
        return resources != null
                ? resources.stream().map(Resource::getId).collect(Collectors.toList())
                : List.of();
    }

    default List<Project> mapProjectIdsToProjects(MomentDto dto) {
        return dto.getProjectIds() != null ? dto.getProjectIds().stream()
                .map(projectId -> {
                    Project project = new Project();
                    project.setId(projectId);
                    return project;
                })
                .collect(Collectors.toList()) : null;
    }

     default List<Resource> mapResourceIdsToResources(MomentDto dto) {
         return dto.getResourceIds() != null ? dto.getResourceIds().stream()
                 .map(resourceId -> {
                     Resource resource = new Resource();
                     resource.setId(resourceId);
                     return resource;
                 })
                 .collect(Collectors.toList()) : null;
     }
}
