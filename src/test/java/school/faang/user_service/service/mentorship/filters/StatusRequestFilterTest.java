package school.faang.user_service.service.mentorship.filters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class StatusRequestFilterTest {

    private StatusRequestFilter statusFiler;
    private RequestFilterDto filterDto;

    @BeforeEach
    public void init() {
        statusFiler = new StatusRequestFilter();
        filterDto = new RequestFilterDto();
    }

    @Test
    public void testIsNotApplicable() {
        assertFalse(statusFiler.isApplicable(filterDto));
    }

    @Test
    public void testIsApplicable() {
        filterDto.setStatus(RequestStatus.ACCEPTED);
        assertTrue(statusFiler.isApplicable(filterDto));
    }

    @Test
    public void testApplyFilter() {
        filterDto.setStatus(RequestStatus.PENDING);
        Stream<MentorshipRequest> requests = Stream.of(
                MentorshipRequest.builder().status(RequestStatus.ACCEPTED).build(),
                MentorshipRequest.builder().status(RequestStatus.PENDING).build(),
                MentorshipRequest.builder().status(RequestStatus.REJECTED).build());

        List<MentorshipRequest> result = statusFiler.apply(requests, filterDto).toList();
        assertEquals(1, result.size());
    }
}
