package kg.peaksoft.taskTrackerb6.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EstimationRequest {

    private LocalDate startDate;
    private MyTimeClassRequest startTime;
    private LocalDate dueDate;
    private MyTimeClassRequest deadlineTime;
    private int reminder;
}
