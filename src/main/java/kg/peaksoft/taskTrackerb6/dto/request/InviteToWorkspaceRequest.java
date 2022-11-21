package kg.peaksoft.taskTrackerb6.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InviteToWorkspaceRequest {

    private String email;
    private Long emailID;
}
