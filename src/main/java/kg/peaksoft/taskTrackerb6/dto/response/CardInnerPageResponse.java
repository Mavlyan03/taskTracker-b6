package kg.peaksoft.taskTrackerb6.dto.response;

import kg.peaksoft.taskTrackerb6.db.model.Card;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardInnerPageResponse {

    private Long id;
    private String title;
    private String description;
    private List<LabelResponse> labelResponses;
    private EstimationResponse estimationResponse;
    private List<MemberResponse> memberResponses;
    private List<ChecklistResponse> checklistResponses;
    private List<CommentResponse> commentResponses;
    private Long columnId;
    private Long boardId;
    private Long workspaceId;


    public CardInnerPageResponse(Card card) {
        this.id = card.getId();
        this.title = card.getTitle();
        this.description = card.getDescription();
        this.columnId = card.getColumn().getId();
        this.boardId = card.getColumn().getBoard().getId();
        this.workspaceId = card.getColumn().getBoard().getWorkspace().getId();
    }
}
