package faang.school.projectservice.dto.moment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class MomentDto {
        private Long id;

        @NotBlank(message = "Field 'name' cannot be empty")
        private String name;
        private String description;
        @NotNull(message = "Field 'date' cannot be empty")
        private LocalDateTime date;
        private List<Long> resourceIds;
        @NotEmpty(message = "You should attach Moment to specific project, 'projectIds' cannot be empty")
        private List<Long> projectIds;
        private List<Long> userIds;
        private String imageId;
}
