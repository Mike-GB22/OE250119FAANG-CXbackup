package faang.school.projectservice.mapper.moment;

import faang.school.projectservice.model.Resource;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ResourceMapperHelper {
    default List<Long> mapResourcesToIds(List<Resource> resources) {
        return resources != null ? resources.stream().map(Resource::getId).collect(Collectors.toList()) : List.of();
    }
}
