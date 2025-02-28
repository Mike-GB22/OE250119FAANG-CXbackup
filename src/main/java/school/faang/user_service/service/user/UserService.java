package school.faang.user_service.service.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.event.EventRepository;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.service.goal.GoalService;
import school.faang.user_service.service.mentorship.MentorshipService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
@Slf4j
public class UserService {

    private GoalService goalService;
    private EventService eventService;
    private EventRepository eventRepository;
    private UserRepository userRepository;
    private MentorshipService mentorshipService;

    private static final String LOG_MESSAGE_DEACTIVATING_STARTS = "Deactivating user with id={}";
    private static final String LOG_MESSAGE_QUIT_PARTICIPATION_IN_GOALS = "Quitting participation in goals for userId = {} goal={}";
    private static final String LOG_MESSAGE_DELETE_EVENTS = "Deleting events for userId={} event={}";
    private static final String ERROR_USER_IS_NOT_PRESENTED_IN_DB = "User is not presented in DB";
    private static final String LOG_MESSAGE_DELETE_GOAL = "Deleting goals fot  userId={} goal={} ";

    public void deactivateUser(Long userId) {
        log.info(LOG_MESSAGE_DEACTIVATING_STARTS, userId);
        User userToDeactivate = getUserFromDataBase(userId);

        quitGoals(userId, userToDeactivate.getGoals());
        stopAndDeleteEvent(userId);
        quitMentorship(userId);

        userToDeactivate.setActive(false);
        userRepository.save(userToDeactivate);
    }

    public User getUser(Long userId) {
        return getUserFromDataBase(userId);
    }

    private void quitGoals(Long userId, List<Goal> userGoals) {
        List<Goal> goalsToDelete = findGoalsToDelete(userId, userGoals);

        for (Goal goal : goalsToDelete) {
            goalService.deleteGoal(goal.getId());
            log.debug(LOG_MESSAGE_DELETE_GOAL, userId, goal.getDescription());
        }

        for (Goal goal : userGoals) {
            List<User> usersOfGoalWithoutDeactivatedUser = goal.getUsers().stream()
                    .filter(u -> !u.getId().equals(userId))
                    .collect(Collectors.toList());
            goal.setUsers(usersOfGoalWithoutDeactivatedUser);
            goalService.updateGoal(userId, goal);
            log.debug(LOG_MESSAGE_QUIT_PARTICIPATION_IN_GOALS, userId, goal.getDescription());
        }
    }

    private void stopAndDeleteEvent(Long userId) {
        List<Event> eventsToDelete = eventRepository.findAllByUserId(userId);
        for (Event event : eventsToDelete) {
            eventService.deleteEvent(event.getId());
            log.debug(LOG_MESSAGE_DELETE_EVENTS, userId, event.getDescription());
        }
    }

    private void quitMentorship(Long userId) {
        mentorshipService.stopMentorship(userId);
    }

    private User getUserFromDataBase(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            log.warn(ERROR_USER_IS_NOT_PRESENTED_IN_DB);
            throw new DataValidationException(ERROR_USER_IS_NOT_PRESENTED_IN_DB);
        }
    }

    private List<Goal> findGoalsToDelete(Long userId, List<Goal> userGoals) {
        List<Goal> goalsToDelete = new ArrayList<>();
        for (Goal goal : userGoals) {
            List<User> users = goal.getUsers();
            if ((users.size() == 1) && (users.get(0).getId().equals(userId))) {
                goalsToDelete.add(goal);
            }
        }
        return goalsToDelete;
    }
}
