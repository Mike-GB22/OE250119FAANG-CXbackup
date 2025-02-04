package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationMapper;
import school.faang.user_service.service.RecommendationService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class RecommendationController {
    RecommendationService recommendationService;

    public RecommendationDto giveRecommendation(RecommendationDto recommendation) {
        if (recommendationDtoIsValid(recommendation)) {
            return recommendationService.create(recommendation);// Как вызывать? Solved   //? как-то вернуть  RecommentationDto object without using 'new'.
        } else {
            throw new DataValidationException("Content is empty");
        }
    }

    public RecommendationDto updateRecommendation(RecommendationDto recommendationDto) {
        if (recommendationDtoIsValid(recommendationDto)) {
            return recommendationService.update(recommendationDto);// Как вызывать? Solved   //? как-то вернуть  RecommentationDto object without using 'new'.
        } else {
            throw new DataValidationException("Content is empty");
        }
    }

    public List<RecommendationDto> getAllUserRecommendations(long receiverId) {
        return recommendationService.getAllUserRecommendations(receiverId);

    }

    public List<RecommendationDto> getAllGivenRecommendations(long authorId) {
        return recommendationService.getAllGivenRecommendations(authorId);
    }

    private void deleteRecommendation(long id) {
        recommendationService.delete(id);
    }

    private boolean recommendationDtoIsValid(RecommendationDto recommendationDto) {
        return !recommendationDto.getContent().isEmpty();
    }
}
