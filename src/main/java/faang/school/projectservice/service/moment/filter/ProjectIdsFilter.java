package faang.school.projectservice.service.moment.filter;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ProjectIdsFilter implements MomentFilter {

    @Override
    public boolean isApplicable(MomentFilterDto filter) {
        return filter.getProjectIdsPattern() != null && !filter.getProjectIdsPattern().isEmpty();
    }

    @Override
    public Stream<Moment> apply(Stream<Moment> momentStream, MomentFilterDto filter) {
        Set<Long> projectIdsPattern = new HashSet<>(filter.getProjectIdsPattern());

        return momentStream
                .filter(moment -> moment.getProjects() != null
                        && moment.getProjects().stream()
                        .anyMatch(project -> projectIdsPattern.contains(project.getId())));
    }
}
