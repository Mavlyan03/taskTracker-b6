package kg.peaksoft.taskTrackerb6.dto.response;

import kg.peaksoft.taskTrackerb6.db.model.Board;
import kg.peaksoft.taskTrackerb6.db.model.Favorite;
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
    private String name;
    private String background;
    private String workspaceOrBoard;
    private boolean isFavorite;

//    public FavoriteResponse(Favorite favorite) {
//        this.id = favorite.getId();
//        if (favorite.getWorkspace() != null) {
//            this.name = favorite.getWorkspace().getName();
//            this.background = "Workspace has not have photo";
//            this.workspaceOrBoard = "Workspace";
//            this.isFavorite = favorite.getWorkspace().getIsFavorite();
//        } else if (favorite.getBoard() != null) {
//            this.name = favorite.getBoard().getTitle();
//            this.background = favorite.getBoard().getBackground();
//            this.workspaceOrBoard = "Board";
//            this.isFavorite = favorite.getBoard().getIsFavorite();
//        }
//    }

    public FavoriteResponse(Workspace workspace) {
        this.id = workspace.getId();
        this.name = workspace.getName();
        this.background = "The workspace can not have photo";
        this.workspaceOrBoard = "WORKSPACE";
        this.isFavorite = workspace.getIsFavorite();
    }

    public FavoriteResponse(Board board) {
        this.id = board.getId();
        this.name = board.getTitle();
        this.background = board.getBackground();
        this.workspaceOrBoard = "BOARD";
        this.isFavorite = board.getIsFavorite();
    }
}
