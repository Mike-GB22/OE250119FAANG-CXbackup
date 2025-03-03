package faang.school.projectservice.service.moment.filter;

import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class ProjectIdsFilterTest {
    private ProjectIdsFilter projectIdFilter;
    private MomentFilterDto filterDto;
    private Stream<Moment> moments;

    @BeforeEach
    public void init() {
        projectIdFilter = new ProjectIdsFilter();
        filterDto = new MomentFilterDto();
        filterDto.setProjectIdsPattern(null);
        Moment moment1 = new Moment();
        Moment moment2 = new Moment();
        Moment moment3 = new Moment();
        Project project1 = new Project();
        project1.setId(1L);
        Project project2 = new Project();
        project2.setId(2L);
        moment1.setProjects(List.of(project1));
        moment2.setProjects(List.of(project2));
        moment3.setProjects(List.of(project1));
        moments = Stream.of(moment1, moment2, moment3);
    }

    @Test
    public void testIsNotApplicable() {
        assertFalse(projectIdFilter.isApplicable(filterDto));
    }

    @Test
    public void testIsNotApplicableEmptyPattern() {
        filterDto.setProjectIdsPattern(List.of());
        assertFalse(projectIdFilter.isApplicable(filterDto));
    }

    @Test
    public void testIsApplicable() {
        filterDto.setProjectIdsPattern(List.of(1L));
        assertTrue(projectIdFilter.isApplicable(filterDto));
    }

    @Test
    public void testApplyFilter() {
        filterDto.setProjectIdsPattern(List.of(1L));
        List<Moment> result = projectIdFilter.apply(moments, filterDto).toList();

        assertEquals(2, result.size());
    }

    @Test
    public void testApplyFilterWithSameProjectIds() {
        filterDto.setProjectIdsPattern(List.of(1L, 2L));
        List<Moment> result = projectIdFilter.apply(moments, filterDto).toList();

        assertEquals(3, result.size());
    }
}
