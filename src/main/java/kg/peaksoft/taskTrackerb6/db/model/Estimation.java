package kg.peaksoft.taskTrackerb6.db.model;

import kg.peaksoft.taskTrackerb6.enums.ReminderType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static javax.persistence.CascadeType.*;

@Entity
@Table(name = "estimations")
@Getter
@Setter
@NoArgsConstructor
public class Estimation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "estimation_gen")
    @SequenceGenerator(name = "estimation_gen", sequenceName = "estimation_seq", allocationSize = 1, initialValue = 2)
    private Long id;

    private LocalDate startDate;

    private LocalDate dueDate;

    private LocalDateTime startTime;

    private LocalDateTime dueTime;

    private ReminderType reminder;

    private LocalDateTime notificationTime;

    @OneToOne(cascade = {DETACH, REFRESH, MERGE})
    private SubTask subTask;

    @OneToOne(cascade = {DETACH, REFRESH, MERGE})
    private Card card;

    @OneToOne(cascade = {DETACH, REFRESH, MERGE})
    private User user;

//    public Estimation(LocalDate startDate, LocalDate dueDate, int reminder) {
//        this.startDate = startDate;
//        this.dueDate = dueDate;
//        this.reminder = reminder;
//    }
}
