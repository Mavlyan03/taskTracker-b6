package kg.peaksoft.taskTrackerb6.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
public class EstimationResponse {

    private Long id;
    private LocalDate startDate;
    private LocalTime startTime;
    private LocalDate dueDate;
    private LocalTime deadlineTime;
    private int reminder;

    public EstimationResponse(Long id, LocalDate startDate, LocalTime startTime, LocalDate dueDate, LocalTime deadlineTime, int reminder) {
        this.id = id;
        this.startDate = startDate;
        this.startTime = startTime;
        this.dueDate = dueDate;
        this.deadlineTime = deadlineTime;
        this.reminder = reminder;
    }
}
