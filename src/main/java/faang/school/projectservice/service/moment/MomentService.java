package faang.school.projectservice.service.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.dto.moment.MomentFilterDto;
import faang.school.projectservice.mapper.moment.MomentMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.MomentRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.moment.filter.MomentFilter;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MomentService {
    private final MomentRepository momentRepository;
    private final MomentMapper momentMapper;
    private final List<MomentFilter> momentFilters;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;

    public Moment save(Moment moment) {
        isAllProjectsActive(moment);

        return momentRepository.save(moment);
    }

    public Optional<Moment> findById(Long id) {
        return momentRepository.findById(id);
    }

    public List<Moment> findAll(MomentFilterDto filter) {
        List<Moment> moments = momentRepository.findAll();

        return !moments.isEmpty() ? filterMoments(moments.stream(), filter) : new ArrayList<>();
    }

    public List<Moment> filterMoments(Stream<Moment> moments, MomentFilterDto filters) {
        if (momentFilters.isEmpty()) {
            return moments.collect(Collectors.toList());
        }

        return momentFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .reduce(moments,
                        (stream, filter) -> filter.apply(stream, filters).stream(),
                        (s1, s2) -> s1)
                .skip((long) filters.getPage() * filters.getPageSize())
                .toList();

    }

    @Transactional
    public void deleteById(Long id) {
        Moment moment = momentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Moment with ID " + id + " not found"));

        Optional.ofNullable(moment.getProjects())
                .ifPresent(projects -> projects
                        .forEach(project -> project.getMoments().remove(moment)));

        momentRepository.delete(moment);
    }

    @Transactional
    public Moment updateMoment(Long momentId, MomentDto momentDto) {
        Moment moment = momentRepository.findById(momentId)
                .orElseThrow(() -> {
                    log.error("Moment with Id {} not found", momentId);
                    return new IllegalArgumentException("Moment with Id " + momentId + " not found");
                });
        log.info("Start update moment");
        momentMapper.updateMomentFromDto(momentDto, moment);
        log.info("Entity created successfully");
        log.info("Moment = {}", moment);
        updateMomentFields(moment, momentDto);
        log.info("newMoment = {}", moment);
        log.info("moment.getUserIds() = {}", moment.getUserIds());
        log.info("moment.getProjects() = {}", moment.getProjects());
        return momentRepository.save(moment);
    }

    private void isAllProjectsActive(Moment moment) {
        List<Long> projectIds = moment.getProjects().stream()
                .map(Project::getId)
                .toList();
        List<Project> projects = projectRepository.findAllById(projectIds);

        boolean allProjectsActive = projects.stream().allMatch(this::isProjectActive);

        if (!allProjectsActive) {
            throw new IllegalArgumentException("All projects must be active to create a Moment.");
        }
    }

    private boolean isProjectActive(Project project) {
        return project.getStatus() == ProjectStatus.IN_PROGRESS;
    }

    private void updateMomentFields(Moment moment, MomentDto momentDto) {
        updateFieldsByProjectIds(moment, momentDto);
        log.info("moment.getUserIds() = {}", moment.getUserIds());
        updateFieldsByUserIds(moment, momentDto);
        log.info("moment.getUserIds() = {}", moment.getUserIds());
    }

    private void updateFieldsByProjectIds(Moment moment, MomentDto momentDto) {
        if (momentDto.getProjectIds() != null && !momentDto.getProjectIds().isEmpty()) {
            List<Project> newProjects = projectRepository.findAllById(momentDto.getProjectIds());
            concatProjects(moment, newProjects);
        }
        log.info("moment.getUserIds() = {}", moment.getUserIds());

        List<Long> newUserIds = getUsersIdsByProjects(moment.getProjects());
        log.info("newUserIds = {}", newUserIds);
        if (newUserIds != null && !newUserIds.isEmpty()) {
            concatUserIds(moment, newUserIds);
        }
        log.info("moment.getUserIds() = {}", moment.getUserIds());
    }

    private void updateFieldsByUserIds(Moment moment, MomentDto momentDto) {
        if (momentDto.getUserIds() != null && !momentDto.getUserIds().isEmpty()) {
            List<Project> newProjects = getProjectsByUsersIds(momentDto.getUserIds());
            concatProjects(moment, newProjects);
            updateFieldsByProjectIds(moment, momentDto);
        }
    }

    private void concatProjects(Moment moment, List<Project> newProjects) {
        List<Project> existingProjects = moment.getProjects();
        List<Project> mergedProjects = Stream.concat(existingProjects.stream(), newProjects.stream())
                .distinct()
                .collect(Collectors.toList());
        moment.setProjects(mergedProjects);
    }

    private void concatUserIds(Moment moment, List<Long> userIds) {
        List<Long> mergedUserIds = Stream.concat(moment.getUserIds().stream(), userIds.stream())
                .distinct()
                .collect(Collectors.toList());
        moment.setUserIds(mergedUserIds);
    }

    private List<Long> getUsersIdsByProjects(List<Project> projects) {
        return projects.stream()
                .flatMap(project -> project.getTeams().stream())
                .flatMap(team -> team.getTeamMembers().stream())
                .map(TeamMember::getUserId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    private List<Project> getProjectsByUsersIds(List<Long> userIds) {
        return userIds.stream()
                .flatMap(userId -> teamMemberRepository.findByUserId(userId).stream())
                .map(TeamMember::getTeam)
                .filter(team -> team != null && team.getProject() != null)
                .map(Team::getProject)
                .distinct()
                .toList();
    }
}
