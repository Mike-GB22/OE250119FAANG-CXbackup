package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.FilterSubProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.filters.FilterProjects;
import faang.school.projectservice.validator.ProjectValidator;
import faang.school.projectservice.validator.SubProjectValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final SubProjectValidator subProjectValidator;
    private final ProjectValidator projectValidator;
    private final List<FilterProjects> projectFilters;

    public Project createSubProject(Long parentId, Project subProject) {
        Optional<Project> optParentProject = projectRepository.findById(parentId);
        projectValidator.doesProjectExist(optParentProject);

        Project parentProject = optParentProject.get();
        subProjectValidator.shouldBePublic(parentProject);
        subProjectValidator.canBeParentProject(parentProject);

        subProject.setParentProject(parentProject);
        subProject.setVisibility(parentProject.getVisibility());
        subProject.setStatus(ProjectStatus.CREATED);
        subProject.setCreatedAt(LocalDateTime.now());

        return projectRepository.save(subProject);
    }

    public Project updateSubProject(Long id, ProjectStatus status, ProjectVisibility visibility) {
        Optional<Project> optSubProject = projectRepository.findById(id);
        projectValidator.doesProjectExist(optSubProject);

        Project subProject = optSubProject.get();
        if (visibility != null) {
            subProject.setVisibility(visibility);
            updateChildVisibility(subProject, visibility);
        }

        if (status != null) {
            if (status.equals(ProjectStatus.COMPLETED)) {
                subProjectValidator.childCompleted(subProject.getChildren());
                //momentService.createMoment(id, "name", updateProject.getChildren())
            }
            subProject.setStatus(status);
            subProject.setUpdatedAt(LocalDateTime.now());
        }

        return projectRepository.save(subProject);
    }

    private void updateChildVisibility(Project project, ProjectVisibility visibility) {
        List<Project> subProjects = project.getChildren();
        if (subProjects != null) {
            subProjects.forEach(subP -> subP.setVisibility(visibility));
        }
    }

    public List<Project> getSubProjects(Long id, FilterSubProjectDto filters, Integer limitList) {
        Optional<Project> parentProject = projectRepository.findById(id);
        projectValidator.doesProjectExist(parentProject);
        subProjectValidator.shouldBePublic(parentProject.get());

        Stream<Project> subProjects = parentProject.get().getChildren().stream();
        return projectFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .reduce(subProjects,
                        (stream, filter) -> filter.apply(stream, filters),
                        (stream1, stream2) -> stream1)
                .filter(project -> !Objects.equals(project.getVisibility(), ProjectVisibility.PRIVATE))
                .limit(limitList)
                .toList();
    }
}
