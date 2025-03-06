package school.faang.user_service.service.mentorship.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.mentorship.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.stream.Stream;

@Component
public class RequesterFilter implements RequestFilter {

    @Override
    public boolean isApplicable(RequestFilterDto filters) {
        return filters.getRequesterId() != null;
    }

    @Override
    public Stream<MentorshipRequest> apply(Stream<MentorshipRequest> mentorshipRequests, RequestFilterDto filters) {
        return mentorshipRequests
                .filter(request -> request.getRequester().getId().equals(filters.getRequesterId()));
    }
}

