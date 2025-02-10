package school.faang.user_service.dto.recommendation;


import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class RecommendationDto {
    private  Long id;
    private  Long authorId;
    private Long receiverId;
    private String content;
    private  List<SkillOfferDto> skillOffers;
    private  LocalDateTime createdAt;

    public RecommendationDto(){
        this.id = null;
        this.authorId = null;
        this.receiverId = null;
        this.content = "";
        this.skillOffers = null;
        this.createdAt  = null;
    }
}
