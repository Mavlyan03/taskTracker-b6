package kg.peaksoft.taskTrackerb6.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

import static javax.persistence.CascadeType.*;
import static javax.persistence.CascadeType.PERSIST;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_gen")
    @SequenceGenerator(name = "notification_gen", sequenceName = "notification_seq",allocationSize = 1)
    private Long id;
    private String text;
    private boolean read=false;

    @ManyToOne(cascade = {DETACH, REFRESH, MERGE, PERSIST})
    private User user;

    @OneToOne(cascade = {DETACH, REFRESH, MERGE, PERSIST})
    private SubTask subTask;

    @OneToOne(cascade = {DETACH, REFRESH, MERGE, PERSIST})
    private Card card;

    @OneToOne(cascade = {DETACH, REFRESH, MERGE, PERSIST})
    private Column column;

    @OneToOne(cascade = {DETACH, REFRESH, MERGE, PERSIST})
    private Estimation estimation;

}
