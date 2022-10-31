package kg.peaksoft.taskTrackerb6.dto.request;

import kg.peaksoft.taskTrackerb6.enums.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InviteRequest {

    private String email;
    private Role role;
    private String link;
    private Long workspaceId;
}
