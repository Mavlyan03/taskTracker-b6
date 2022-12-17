package kg.peaksoft.taskTrackerb6.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class EstimationRequest {

    private LocalDate startDate;
    private LocalDate dueDate;
    private LocalDateTime startTime;
    private LocalDateTime dueTime;
    private String reminder;
}
