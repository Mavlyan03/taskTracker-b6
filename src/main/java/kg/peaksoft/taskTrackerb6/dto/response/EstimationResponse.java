package kg.peaksoft.taskTrackerb6.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class EstimationResponse {

    private Long id;
    private LocalDateTime startDate;
    private LocalDateTime deadlineDate;
    private int reminder;

    public EstimationResponse(Long id, LocalDateTime startDate, LocalDateTime deadlineDate, int reminder) {
        this.id = id;
        this.startDate = startDate;
        this.deadlineDate = deadlineDate;
        this.reminder = reminder;
    }
}
