package kg.peaksoft.taskTrackerb6.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EstimationRequest {

    private String startDate;
    private String dueDate;
    private String startTime;
    private String dueTime;
    private String reminder;
}
