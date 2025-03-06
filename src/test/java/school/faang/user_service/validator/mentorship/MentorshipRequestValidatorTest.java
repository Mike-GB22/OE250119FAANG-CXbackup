package school.faang.user_service.validator.mentorship;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class MentorshipRequestValidatorTest {

    @Mock
    private MentorshipRequestRepository requestRepository;

    @InjectMocks
    private MentorshipRequestValidator requestValidator;


    @Test
    public void testNotValidIntervalBetweenRequests() {
        long requesterId = 5L;
        long receiverId = 7L;
        int monthsInterval = 2;
        MentorshipRequest testRequest = testDataInterval(requesterId, receiverId, monthsInterval);

        when(requestRepository.findLatestRequest(requesterId, receiverId))
                .thenReturn(Optional.of(testRequest));

        assertFalse(requestValidator.validateLastRequestData(requesterId, receiverId));
    }

    @Test
    public void testValidIntervalBetweenRequests() {
        long requesterId = 5L;
        long receiverId = 7L;
        int monthsInterval = 4;
        MentorshipRequest testRequest = testDataInterval(requesterId, receiverId, monthsInterval);

        when(requestRepository.findLatestRequest(requesterId, receiverId))
                .thenReturn(Optional.of(testRequest));

        assertTrue(requestValidator.validateLastRequestData(requesterId, receiverId));
    }

    @Test
    public void testIsMentorAlready() {
        User secondUser = User.builder().id(2L).build();
        User firstUser = User
                .builder()
                .id(1L)
                .mentors(List.of(secondUser))
                .build();

        assertThrows(
                IllegalArgumentException.class,
                () -> requestValidator.validateNotMentorYet(firstUser, secondUser));
    }

    @Test
    public void testNotInMentorsList() {
        User secondUser = User.builder().id(2L).build();
        User mentor = User.builder().id(3L).build();
        User firstUser = User.builder().id(1L).mentors(List.of(mentor)).build();

        assertTrue(requestValidator.validateNotMentorYet(firstUser, secondUser));
    }

    private MentorshipRequest testDataInterval(long requesterId, long receiverId, int monthsTime) {
        User firstUser = User.builder().id(requesterId).build();
        User secondUser = User.builder().id(receiverId).build();
        LocalDateTime testTme = LocalDateTime.now().minusMonths(monthsTime);

        return MentorshipRequest
                .builder()
                .receiver(firstUser)
                .requester(secondUser)
                .createdAt(testTme)
                .build();
    }
}
