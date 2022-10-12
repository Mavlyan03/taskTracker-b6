package kg.peaksoft.taskTrackerb6.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

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

    @OneToOne
    private Card card;

    @OneToOne
    private SubTask subTask;

    @OneToOne(cascade = CascadeType.ALL)
    private User user;
}
