package kg.peaksoft.taskTrackerb6.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkspaceResponse {

    private Long id;
    private String name;
    private UserResponse lead;
    private boolean action;
}
