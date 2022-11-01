package kg.peaksoft.taskTrackerb6.db.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.*;

import static javax.persistence.CascadeType.*;

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
    private kg.peaksoft.taskTrackerb6.db.model.Column column;

    @OneToOne(cascade = {DETACH, REFRESH, MERGE, PERSIST})
    private Estimation estimation;

}
