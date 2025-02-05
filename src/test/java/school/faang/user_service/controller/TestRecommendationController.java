package school.faang.user_service.controller;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.recommendation.RecommendationController;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.RecommendationService;

import java.time.LocalDateTime;
import java.util.ArrayList;

@ExtendWith(MockitoExtension.class)
public class TestRecommendationController {

    @Mock
    private RecommendationDto recommendationDto;

    @Mock
    private RecommendationService recommendationService;

    @InjectMocks
    private RecommendationController recommendationController;

    @Test
    public void testRecommendationDtoContentIsValid() {
//        Mockito.when(recommendationService.create(Mockito.any())).thenReturn(null);
//        recommendationController.giveRecommendation(new RecommendationDto())
        RecommendationDto recommendationDto1 = new RecommendationDto(
                1L,
                1L,
                2L,
                "SQL",
                new ArrayList<SkillOfferDto>(),
                LocalDateTime.now());
        Mockito.when(recommendationController.giveRecommendation(recommendationDto1)).thenReturn(recommendationDto1);
        Mockito.verify(recommendationService.create(recommendationDto1));

    }
    @Test
    public void testRecommendationDtoContentIsInvalid() {
        Assert.assertThrows(DataValidationException.class,
                () -> recommendationController.giveRecommendation(new RecommendationDto(
                        1L,
                        1L,
                        2L,
                        "",
                        new ArrayList<SkillOfferDto>(),
                        LocalDateTime.now())
                ));
    }

}
