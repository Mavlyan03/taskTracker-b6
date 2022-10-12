package kg.peaksoft.taskTrackerb6.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

import static javax.persistence.CascadeType.*;

@Entity
@Table(name = "estimations")
@Getter
@Setter
@NoArgsConstructor
public class Estimation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "estimation_gen")
    @SequenceGenerator(name = "estimation_gen", sequenceName = "estimation_seq", allocationSize = 1)
    private Long id;

    private LocalDate created;

    private LocalDate deadline;

    private int reminder;

    private String text;

    @OneToOne(cascade = {DETACH, REFRESH, PERSIST, MERGE})
    private Card card;

    @OneToOne(cascade = {DETACH, REFRESH, PERSIST, MERGE})
    private SubTask subTask;

    @OneToOne(cascade = {DETACH, REFRESH, PERSIST, MERGE})
    private User user;
}
