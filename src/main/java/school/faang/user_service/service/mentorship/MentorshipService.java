package school.faang.user_service.service.mentorship;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.repository.mentorship.MentorshipRepository;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class MentorshipService {
    private GoalRepository goalRepository;
    private MentorshipRepository mentorshipRepository;

    /**
     * - deletes mentees from mentor
     * - changes mentee_id of goals created by mentor
     * to id of first user associated with the goal.
     *
     * @param userToDeactivateId - mentor who is being deactivated
     */
    public void stopMentorship(Long userToDeactivateId) {
        List<Goal> goalsControlledByMentor = goalRepository.findGoalsByMentorId(userToDeactivateId);
        for (Goal goal : goalsControlledByMentor) {
            List<User> usersOfGoalWithoutDeactivatedUser = goal.getUsers().stream()
                    .filter(u -> !u.getId().equals(userToDeactivateId))
                    .collect(Collectors.toList());
            goal.setMentor(usersOfGoalWithoutDeactivatedUser.get(0));
            log.debug("Goal \"{}\" has changed mentor_id: old Id={} , new Id=\"{}\"",
                    goal.getDescription(),
                    userToDeactivateId,
                    usersOfGoalWithoutDeactivatedUser.get(0).getId());
        }
        goalRepository.saveAll(goalsControlledByMentor);

        mentorshipRepository.deleteByMentorId(userToDeactivateId);
        log.debug("Deleting mentees for mentor with mentor_id={}", userToDeactivateId);
    }
}
