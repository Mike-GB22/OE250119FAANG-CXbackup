package school.faang.user_service.service.mentorship.filters;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReceiverRequestFilterTest {

    private ReceiverRequestFilter receiverFilter;
    private RequestFilterDto filterDto;
    private Stream<MentorshipRequest> requests;

    @BeforeEach
    public void testDataInit() {
        User firstUser = User.builder().id(1L).build();
        User secondUser = User.builder().id(2L).build();

        receiverFilter = new ReceiverRequestFilter();
        filterDto = new RequestFilterDto();
        filterDto.setReceiverId(secondUser.getId());

        requests = Stream.of(
                MentorshipRequest.builder().receiver(firstUser).build(),
                MentorshipRequest.builder().receiver(secondUser).build());
    }

    @Test
    public void testIsNotApplicable() {
        assertFalse(receiverFilter.isApplicable(new RequestFilterDto()));
    }

    @Test
    public void testIsApplicable() {
        assertTrue(receiverFilter.isApplicable(filterDto));
    }

    @Test
    public void testApplyFilter() {
        List<MentorshipRequest> result = receiverFilter.apply(requests, filterDto).toList();

        assertEquals(1, result.size());
        result.forEach(request -> assertEquals(request.getReceiver().getId(), filterDto.getReceiverId()));
    }
}

