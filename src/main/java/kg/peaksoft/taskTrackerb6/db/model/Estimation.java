package kg.peaksoft.taskTrackerb6.db.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.persistence.Column;
import java.time.LocalDate;
import java.time.LocalTime;

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

    private LocalTime startTime;

    private LocalDate dueDate;

    private LocalTime deadlineTime;

    private int reminder;

    @Column(length = 10000)
    private String text;

    @OneToOne(cascade = {DETACH, REFRESH, PERSIST, MERGE})
    private Card card;

    @OneToOne(cascade = {DETACH, REFRESH, PERSIST, MERGE})
    private SubTask subTask;

    @OneToOne(cascade = {DETACH, REFRESH, PERSIST, MERGE})
    private User user;

    public Estimation(LocalDate startDate, LocalTime startTime, LocalDate dueDate, LocalTime deadlineTime, int reminder) {
        this.startDate = startDate;
        this.startTime = startTime;
        this.dueDate = dueDate;
        this.deadlineTime = deadlineTime;
        this.reminder = reminder;
    }
}
