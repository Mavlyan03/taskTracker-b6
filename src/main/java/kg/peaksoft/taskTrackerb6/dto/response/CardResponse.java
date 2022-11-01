package kg.peaksoft.taskTrackerb6.dto.response;

import kg.peaksoft.taskTrackerb6.db.model.Card;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CardResponse {

    private Long id;
    private String title;
//    private String description;
//    private List<LabelResponse> labelResponses;
//    private EstimationResponse estimationResponse;
//    private MemberResponse memberResponse;
//    private ChecklistResponse checklistResponse;
//    private List<CommentResponse> commentResponses;

    public CardResponse(Card card) {
        this.id = card.getId();
        this.title = card.getTitle();
    }
    
}
