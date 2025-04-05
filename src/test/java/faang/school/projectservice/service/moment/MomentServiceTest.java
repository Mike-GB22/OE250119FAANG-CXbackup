package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.mapper.moment.MomentMapperImpl;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.moment.filter.MomentFilter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@Slf4j
@ExtendWith(MockitoExtension.class)
public class MomentServiceTest {
    @Mock
    private MomentRepository momentRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private MomentFilter startMomentFilter;


    @Spy
    private MomentMapperImpl momentMapper;

    @InjectMocks
    private MomentService momentService;

    private List<MomentFilter> momentFilters;

    @Test
    public void saveMomentWithActiveProjectsShouldSaveSuccessfully() {
        Project activeProject = new Project();
        activeProject.setId(1L);
        activeProject.setStatus(ProjectStatus.IN_PROGRESS);

        Moment moment = new Moment();
        moment.setProjects(List.of(activeProject));

        when(projectRepository.findAllById(List.of(1L))).thenReturn(List.of(activeProject));
        when(momentRepository.save(moment)).thenReturn(moment);

        Moment savedMoment = momentService.save(moment);

        assertNotNull(savedMoment);
        verify(momentRepository, times(1)).save(moment);
    }

    @Test
    void saveMomentWithInactiveProjectsShouldThrowException() {
        Project inactiveProject = new Project();
        inactiveProject.setId(2L);
        inactiveProject.setStatus(ProjectStatus.COMPLETED);

        Moment moment = new Moment();
        moment.setProjects(List.of(inactiveProject));

        when(projectRepository.findAllById(List.of(2L))).thenReturn(List.of(inactiveProject));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> momentService.save(moment));
        assertEquals("All projects must be active to create a Moment.", exception.getMessage());
    }

    @Test
    void findByIdShouldReturnMomentWhenFound() {
        Moment moment = new Moment();
        moment.setId(1L);

        when(momentRepository.findById(1L)).thenReturn(Optional.of(moment));

        Optional<Moment> result = momentService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void findByIdShouldReturnEmptyWhenNotFound() {
        when(momentRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Moment> result = momentService.findById(99L);

        assertFalse(result.isPresent());
    }

    @Test
    void findAllShouldReturnEmptyListWhenNoMomentsAvailable() {
        when(momentRepository.findAll()).thenReturn(List.of());

        List<Moment> result = momentService.findAll(new MomentFilterDto());

        verify(momentRepository, times(1)).findAll();
        assertTrue(result.isEmpty());
    }

    @Test
    void findAllShouldReturnFilteredList() {
        Moment firstMoment = new Moment();
        Moment secondMoment = new Moment();
        List<Moment> moments = List.of(firstMoment, secondMoment);
        MomentFilterDto filter = new MomentFilterDto();
        filter.setDateEndPattern(YearMonth.now());

        when(momentRepository.findAll()).thenReturn(moments);
        when(startMomentFilter.isApplicable(any())).thenReturn(true);
        when(startMomentFilter.apply(any(), any())).thenAnswer(invocation -> invocation.getArgument(0));

        momentFilters = List.of(startMomentFilter);

        momentService = new MomentService(momentRepository, momentMapper, momentFilters, projectRepository, teamMemberRepository);

        List<Moment> result = momentService.findAll(filter);

        verify(momentRepository, times(1)).findAll();
        assertEquals(result, moments);
    }


    @Test
    void updateMomentShouldUpdateSuccessfully() {
        String newName = "New Name";

        Moment existingMoment = new Moment();
        existingMoment.setDate(LocalDateTime.now());
        existingMoment.setUserIds(List.of());
        Project project = new Project();
        Team team = new Team();
        TeamMember teamMember = new TeamMember();
        teamMember.setId(1L);
        team.setTeamMembers(List.of(teamMember));
        project.setTeams(List.of(team));
        existingMoment.setUserIds(List.of(2L));
        existingMoment.setId(1L);
        existingMoment.setProjects(List.of(project));
        existingMoment.setName("Old Name");

        MomentDto momentDto = new MomentDto();
        momentDto.setName(newName);
        momentDto.setProjectIds(List.of(2L));
        momentDto.setUserIds(List.of(1L));

        when(momentRepository.findById(1L)).thenReturn(Optional.of(existingMoment));
        when(momentRepository.save(existingMoment)).thenReturn(existingMoment);

        momentService.updateMoment(1L, momentDto);

        System.out.println(existingMoment);

        verify(momentRepository, times(1)).findById(1L);
        verify(momentMapper, times(1)).updateMomentFromDto(momentDto, existingMoment);
        verify(momentRepository, times(1)).save(existingMoment);
    }

    @Test
    void updateMomentShouldThrowExceptionWhenMomentNotFound() {
        MomentDto momentDto = new MomentDto();

        when(momentRepository.findById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> momentService.updateMoment(99L, momentDto));
        assertEquals("Moment with Id 99 not found", exception.getMessage());
    }

    @Test
    void deleteByIdShouldThrowExceptionWhenMomentNotFound() {
        doReturn(Optional.empty()).when(momentRepository).findById(99L);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> momentService.deleteById(99L));
        assertEquals("Moment with ID 99 not found", exception.getMessage());
    }

    @Test
    void deleteByIdShouldDeleteMomentWhenExists() {
        Moment moment = new Moment();

        moment.setId(1L);

        when(momentRepository.findById(1L)).thenReturn(Optional.of(moment));
        doNothing().when(momentRepository).delete(moment);

        momentService.deleteById(1L);

        verify(momentRepository, times(1)).delete(moment);
    }
}
