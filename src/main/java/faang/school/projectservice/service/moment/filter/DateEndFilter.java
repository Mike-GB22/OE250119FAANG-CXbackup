package faang.school.projectservice.service.moment.filter;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class DateEndFilter implements MomentFilter {

    @Override
    public boolean isApplicable(MomentFilterDto filter) {
        return filter.getDateEndPattern() != null;
    }

    @Override
    public List<Moment> apply(Stream<Moment> momentSystem, MomentFilterDto filter) {
        return momentSystem
                .filter(moment -> YearMonth
                        .from(moment.getDate()).isBefore(filter.getDateEndPattern()))
                .collect(Collectors.toList());
    }
}
