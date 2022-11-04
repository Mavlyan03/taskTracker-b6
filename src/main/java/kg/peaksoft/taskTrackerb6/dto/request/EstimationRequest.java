package kg.peaksoft.taskTrackerb6.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class EstimationRequest {

    private LocalDateTime startDate;
    private LocalDateTime deadlineDate;
    private int reminder;
}
