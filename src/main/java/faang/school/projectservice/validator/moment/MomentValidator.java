package faang.school.projectservice.validator.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.exception.DataValidationException;
import org.springframework.stereotype.Component;

@Component
public class MomentValidator {
    public void validateCreateMoment(MomentDto newMoment) {
//        if (newMoment.getName() == null) {
//            throw new DataValidationException("Field 'name' cannot be empty");
//        }

//        if (newMoment.getProjectIds().isEmpty()) {
//            throw new DataValidationException("You should attach Moment to specific project, 'projectIds' cannot be empty");
//        }

//        if (newMoment.getResourceIds().isEmpty()) {
//            throw new DataValidationException("You should attach Moment to specific resource, 'resourceIds' cannot be empty");
//        }

//        if (newMoment.getDate() == null) {
//            throw new DataValidationException("Field 'date' cannot be empty");
//        }
    }
}
