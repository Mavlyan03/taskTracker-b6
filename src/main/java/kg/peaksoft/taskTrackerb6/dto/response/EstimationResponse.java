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
//    private LocalDate startDate;
//    private LocalDate dueDate;
    private LocalDateTime startDateTime;
    private LocalDateTime dueDateTime;
    private ReminderType reminder;

    public EstimationResponse(Long id, LocalDateTime startDateTime, LocalDateTime dueDateTime, ReminderType reminder) {
        this.id = id;
//        this.startDate = startDate;
//        this.dueDate = dueDate;
        this.startDateTime = startDateTime;
        this.dueDateTime = dueDateTime;
        this.reminder = reminder;
    }
}
