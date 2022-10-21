package kg.peaksoft.taskTrackerb6.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FavoriteBoardResponse {

    private Long id;
    private String title;
    private String photo;
    private boolean isFavorite;

}
