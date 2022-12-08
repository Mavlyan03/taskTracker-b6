package kg.peaksoft.taskTrackerb6.dto.response;

import kg.peaksoft.taskTrackerb6.db.model.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ColumnResponse {

    private Long id;
    private String columnName;
    private Boolean isArchived;
    private Long boardId;
    private Long workspaceId;
    private CreatorResponse creator;
    private List<CardResponse> columnCards;

    public ColumnResponse(Column column) {
        this.id = column.getId();
        this.columnName = column.getTitle();
        this.isArchived = column.getIsArchive();
        this.boardId = column.getBoard().getId();
        this.workspaceId = column.getBoard().getWorkspace().getId();
    }
}
