package faang.school.projectservice.service.filters;

import faang.school.projectservice.dto.project.FilterSubProjectDto;
import faang.school.projectservice.model.Project;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Stream;

@Slf4j
@Component
public class StatusProjectFilter implements FilterProjects {

    @Override
    public boolean isApplicable(FilterSubProjectDto filters) {
        return filters.getStatus() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> subProjects, FilterSubProjectDto filters) {
        return subProjects
                .filter(subs -> Objects.equals(subs.getStatus(), filters.getStatus()));
    }
}
