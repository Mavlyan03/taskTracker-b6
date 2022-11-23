package kg.peaksoft.taskTrackerb6.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class WorkspaceRequest {

    private String name;
    private List<String> emails;
    private String link;

}