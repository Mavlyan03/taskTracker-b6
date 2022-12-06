package kg.peaksoft.taskTrackerb6.dto.response;

import kg.peaksoft.taskTrackerb6.db.model.Card;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CardResponse {

    private Long id;
    private String title;
    private List<LabelResponse> labelResponses;
    private String duration;
    private int numberOfMembers;
    private int numberOfSubTasks;
    private int numberOfCompletedSubTask;
    private CreatorResponse creator;
    private Boolean isArchive;
    private Long columnId;
    private Long boardId;
    private Long workspaceId;

    public CardResponse(Card card) {
        this.id = card.getId();
        this.title = card.getTitle();
        this.columnId = card.getColumn().getId();
        this.boardId = card.getColumn().getBoard().getId();
        this.workspaceId = card.getColumn().getBoard().getWorkspace().getId();
        this.isArchive = card.getIsArchive();
    }
}
