package faang.school.projectservice.service.moment.filter;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
public interface MomentFilter {
    boolean isApplicable(MomentFilterDto filters);

    Stream<Moment> apply(Stream<Moment> moments, MomentFilterDto filters);
}
