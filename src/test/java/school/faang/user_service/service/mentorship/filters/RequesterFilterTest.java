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

public class RequesterFilterTest {

    private RequesterFilter requesterFilter;
    private RequestFilterDto filterDto;
    private Stream<MentorshipRequest> requests;

    @BeforeEach
    public void testDataInit() {
        User firstUser = User.builder().id(1L).build();
        User secondUser = User.builder().id(2L).build();

        requesterFilter = new RequesterFilter();
        filterDto = new RequestFilterDto();
        filterDto.setRequesterId(firstUser.getId());

        requests = Stream.of(
                MentorshipRequest.builder().requester(firstUser).build(),
                MentorshipRequest.builder().requester(secondUser).build());
    }

    @Test
    public void testIsNotApplicable() {
        assertFalse(requesterFilter.isApplicable(new RequestFilterDto()));
    }

    @Test
    public void testIsApplicable() {
        assertTrue(requesterFilter.isApplicable(filterDto));
    }

    @Test
    public void testApplyFilter() {
        List<MentorshipRequest> result = requesterFilter.apply(requests, filterDto).toList();

        assertEquals(1, result.size());
        result.forEach(request -> assertEquals(request.getRequester().getId(), filterDto.getRequesterId()));
    }
}
