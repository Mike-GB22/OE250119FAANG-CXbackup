package school.faang.user_service.validator.subscription;

import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;

@Component
public class SubscriptionValidator {
    public void validateFollowUserIds(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException("You can't subscribe to yourself");
        }
    }

    public void validateUnfollowUserIds(long followerId, long followeeId) {
        if (followerId == followeeId) {
            throw new DataValidationException("You can't unsubscribe from yourself");
        }
    }
}
