package kg.peaksoft.taskTrackerb6.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class FavoritesResponse {

    private List<FavoriteWorkspaceResponse> favoriteWorkspaceResponses;
    private List<FavoriteBoardResponse> favoriteBoardResponses;

}
