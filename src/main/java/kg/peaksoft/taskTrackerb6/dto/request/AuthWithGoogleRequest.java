package kg.peaksoft.taskTrackerb6.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthWithGoogleRequest {

    private String token;
    private Boolean isAdmin;
    private Boolean isBoard;
    private Long workspaceOrBoardId;
}
