package kg.peaksoft.taskTrackerb6.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
public class EstimationRequest {

    private LocalDate startDate;
    private LocalTime startTime;
    private LocalDate dueDate;
    private LocalTime deadlineTime;
    private int reminder;
}
