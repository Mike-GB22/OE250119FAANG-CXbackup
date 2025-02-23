package faang.school.projectservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class VacancyDto {
    private Long id;
    private String name;
    private String description;
    private Integer positionId;
    private Long projectId;
    private Double salary;
    private String coverImageKey;
    private List<Long> requiredSkillIds;
    private Integer count;
    private List<Long> candidatesIds;
    private Integer statusId;
    private Long roleId;
}

