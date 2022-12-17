package kg.peaksoft.taskTrackerb6.dto.response;

import kg.peaksoft.taskTrackerb6.enums.ReminderType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class EstimationResponse {

    private Long id;
    private LocalDate startDate;
    private LocalDate dueDate;
    private LocalDateTime startTime;
    private LocalDateTime dueTime;
    private ReminderType reminder;

    public EstimationResponse(Long id, LocalDate startDate, LocalDate dueDate, LocalDateTime startTime, LocalDateTime dueTime, ReminderType reminder) {
        this.id = id;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.startTime = startTime;
        this.dueTime = dueTime;
        this.reminder = reminder;
    }
}
