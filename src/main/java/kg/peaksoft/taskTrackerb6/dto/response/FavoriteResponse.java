package kg.peaksoft.taskTrackerb6.dto.response;

import kg.peaksoft.taskTrackerb6.db.model.Board;
import kg.peaksoft.taskTrackerb6.db.model.Workspace;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteResponse {

    private Long id;
    private Long workspaceOrBoardID;
    private String name;
    private String background;
    private String workspaceOrBoard;
    private boolean isFavorite;


    public FavoriteResponse(Workspace workspace) {
        this.id = workspace.getId();
        this.workspaceOrBoardID = workspace.getId();
        this.name = workspace.getName();
        this.background = "The workspace can not have photo";
        this.workspaceOrBoard = "WORKSPACE";
        this.isFavorite = true;
    }

    public FavoriteResponse(Board board) {
        this.id = board.getId();
        this.workspaceOrBoardID = board.getId();
        this.name = board.getTitle();
        this.background = board.getBackground();
        this.workspaceOrBoard = "BOARD";
        this.isFavorite = true;
    }
}
