package kg.peaksoft.taskTrackerb6.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubTaskRequest {

    private Boolean isDone;
    private String description;
}
