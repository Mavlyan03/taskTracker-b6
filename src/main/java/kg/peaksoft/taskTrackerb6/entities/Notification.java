package kg.peaksoft.taskTrackerb6.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.persistence.Column;

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
    @SequenceGenerator(name = "notification_gen", sequenceName = "notification_seq", allocationSize = 1, initialValue = 2)
    private Long id;

    @Column(length = 10000)
    private String text;

    private boolean isRead = false;

    @ManyToOne(cascade = {DETACH, REFRESH, MERGE, PERSIST})
    private User user;

    @OneToOne(cascade = {DETACH, REFRESH, MERGE, PERSIST})
    private SubTask subTask;

    @OneToOne(cascade = {DETACH, REFRESH, MERGE, PERSIST})
    private Card card;

    @OneToOne(cascade = {DETACH, REFRESH, MERGE, PERSIST})
    private Line line;

    @OneToOne(cascade = {DETACH, REFRESH, MERGE, PERSIST})
    private Estimation estimation;

}
