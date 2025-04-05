package faang.school.projectservice.mapper.moment;

import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProjectMapperHelper {
    default List<Long> mapProjectsToIds(List<Project> projects) {
        return projects != null ? projects.stream().map(Project::getId).collect(Collectors.toList()) : List.of();
    }
}
