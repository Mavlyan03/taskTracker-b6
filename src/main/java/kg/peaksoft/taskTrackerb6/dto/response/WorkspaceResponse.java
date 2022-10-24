package kg.peaksoft.taskTrackerb6.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class WorkspaceResponse {

    private Long id;
    private String name;
    private CreatorResponse lead;
    private boolean action;
}
