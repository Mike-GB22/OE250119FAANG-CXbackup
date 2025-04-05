package faang.school.projectservice.dto.moment;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;
import java.util.List;

@Data
@NoArgsConstructor
public class MomentFilterDto {
    YearMonth dateStartPattern;
    YearMonth dateEndPattern;
    List<Long> projectIdsPattern;
    int page = 0;
    int pageSize = 10;
}
