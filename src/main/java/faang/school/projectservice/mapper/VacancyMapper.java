package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.VacancyDto;
import faang.school.projectservice.model.*;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface VacancyMapper {
    @Mappings({
            @Mapping(target = "project", source = "projectId", qualifiedByName = "mapProjectIdToProject"),
            @Mapping(target = "position", source = "positionId", qualifiedByName = "mapPositionIdToPosition"),
            @Mapping(target = "status", source = "statusId", qualifiedByName = "mapStatusIdToId"),
            @Mapping(target = "candidates", source = "candidatesIds", qualifiedByName = "mapCandidatesIdToCandidates"),
            @Mapping(target = "createdBy", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),
            @Mapping(target = "updatedBy", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "workSchedule", ignore = true),
    })
    Vacancy toEntity(VacancyDto dto);

    @Mappings({
            @Mapping(target = "projectId", source = "project", qualifiedByName = "mapProjectToProjectId"),
            @Mapping(target = "positionId", source = "position", qualifiedByName = "mapPositionToPositionId"),
            @Mapping(target = "statusId", source = "status", qualifiedByName = "mapStatusToStatusId"),
            @Mapping(target = "candidatesIds", source = "candidates", qualifiedByName = "mapCandidatesToCandidatesId"),
            @Mapping(target = "roleId", ignore = true),
    })
    VacancyDto toDto(Vacancy entity);

    @Mappings({
            @Mapping(target = "project", source = "projectId", qualifiedByName = "mapProjectIdToProject"),
            @Mapping(target = "position", source = "positionId", qualifiedByName = "mapPositionIdToPosition"),
            @Mapping(target = "status", source = "statusId", qualifiedByName = "mapStatusIdToId"),
            @Mapping(target = "candidates", source = "candidatesIds", qualifiedByName = "mapCandidatesIdToCandidates"),
            @Mapping(target = "createdBy", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),
            @Mapping(target = "updatedBy", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "workSchedule", ignore = true),
    })
    Vacancy update(@MappingTarget Vacancy vacancy, VacancyDto vacancyDto);

    @Named("mapProjectIdToProject")
    default Project mapProjectIdToProject(Long projectId) {
        if (projectId == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        return Project.builder().id(projectId).build();
    }
    @Named("mapPositionIdToPosition")
    default TeamRole mapPositionIdToPosition(Integer someInt) {
        if (someInt == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        return TeamRole.getAll().get(someInt);
    }

    @Named("mapStatusIdToId")
    default VacancyStatus mapStatusIdToId(Integer someInt) {
        if (someInt == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        return VacancyStatus.getAll().get(someInt);
    }

    @Named("mapCandidatesIdToCandidates")
    default List<Candidate> mapCandidatesIdToCandidates(List<Long> candidates) {
        if (candidates == null) {
            throw new IllegalArgumentException("Candidates cannot be null");
        }
        return candidates.stream()
                .map(candidateId -> {
                    Candidate candidate = new Candidate();
                    candidate.setId(candidateId);
                    return candidate;
                })
                .toList();
    }
    @Named("mapPositionToPositionId")
    default Integer map5(TeamRole someTeamRole) {
        if (someTeamRole == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        return someTeamRole.ordinal();
    }

    @Named("mapProjectToProjectId")
    default Long mapProjectToProjectId(Project project) {
        if (project == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        return project.getId();
    }

    @Named("mapStatusToStatusId")
    default Integer mapStatusToStatusId(VacancyStatus someStatus) {
        if (someStatus == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        return someStatus.ordinal();
    }

    @Named("mapCandidatesToCandidatesId")
    default List<Long> mapCandidatesToCandidatesId(List<Candidate> candidates) {
        if (candidates == null) {
            throw new IllegalArgumentException("Candidates cannot be null");
        }
        return candidates.stream()
                .map(Candidate::getId)
                .toList();
    }


}
