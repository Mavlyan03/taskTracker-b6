package kg.peaksoft.taskTrackerb6.dto.response;

import kg.peaksoft.taskTrackerb6.db.model.Board;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BoardResponse {

    private Long id;
    private String title;
    private Boolean isFavorite;
    private String background;

    public BoardResponse(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.isFavorite = board.getIsFavorite();
        this.background = board.getBackground();
    }
}