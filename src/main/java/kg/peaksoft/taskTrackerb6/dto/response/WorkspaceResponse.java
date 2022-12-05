package kg.peaksoft.taskTrackerb6.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class WorkspaceResponse {

    private Long id;
    private String name;
    private CreatorResponse lead;
    private Boolean action;

    public WorkspaceResponse(Long id, String name, Boolean action) {
        this.id = id;
        this.name = name;
        this.action = action;
    }
}
