package kg.peaksoft.taskTrackerb6.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EstimationRequest {

    private LocalDate createdDate;
    private LocalDate deadlineDate;
    private int reminder;
}
