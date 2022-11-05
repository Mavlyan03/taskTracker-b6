package kg.peaksoft.taskTrackerb6.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EstimationResponse {

    private Long id;
    private LocalDate startDate;
    private MyTimeClassResponse startTime;
    private LocalDate dueDate;
    private MyTimeClassResponse deadlineTime;
    private int reminder;

    public EstimationResponse(Long id, LocalDate startDate, MyTimeClassResponse startTime, LocalDate dueDate, MyTimeClassResponse deadlineTime, int reminder) {
        this.id = id;
        this.startDate = startDate;
        this.startTime = startTime;
        this.dueDate = dueDate;
        this.deadlineTime = deadlineTime;
        this.reminder = reminder;
    }
}
