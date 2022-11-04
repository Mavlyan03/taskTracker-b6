package kg.peaksoft.taskTrackerb6.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EstimationResponse {

    private Long id;
    private LocalDate createdDate;
    private LocalDate deadlineDate;
    private int reminder;

    public EstimationResponse(Long id, LocalDate createdDate, LocalDate deadlineDate, int reminder) {
        this.id = id;
        this.createdDate = createdDate;
        this.deadlineDate = deadlineDate;
        this.reminder = reminder;
    }
}
