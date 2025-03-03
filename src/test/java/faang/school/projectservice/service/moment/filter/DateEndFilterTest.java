package faang.school.projectservice.service.moment.filter;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DateEndFilterTest {
    private DateEndFilter dateEndFilter;
    private MomentFilterDto filterDto;
    private Stream<Moment> moments;

    @BeforeEach
    public void init() {
        dateEndFilter = new DateEndFilter();
        filterDto = new MomentFilterDto();
        filterDto.setDateEndPattern(null);
        Moment moment1 = new Moment();
        Moment moment2 = new Moment();
        Moment moment3 = new Moment();
        moment1.setDate(LocalDateTime.parse("2021-01-01T00:00:00"));
        moment2.setDate(LocalDateTime.parse("2021-02-01T00:00:00"));
        moment3.setDate(LocalDateTime.parse("2021-03-01T00:00:00"));
        moments = Stream.of(moment1, moment2, moment3);
    }

    @Test
    public void testIsNotApplicable() {
        assertFalse(dateEndFilter.isApplicable(filterDto));
    }

    @Test
    public void testIsApplicable() {
        filterDto.setDateEndPattern(YearMonth.parse("2021-01"));
        assertTrue(dateEndFilter.isApplicable(filterDto));
    }

    @Test
    public void testApplyFilter() {
        filterDto.setDateEndPattern(YearMonth.parse("2021-02"));
        List<Moment> result = dateEndFilter.apply(moments, filterDto).toList();

        assertEquals(1, result.size());
    }
}


