package kg.peaksoft.taskTrackerb6.db.model;


import kg.peaksoft.taskTrackerb6.enums.NotificationType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.*;
import java.time.LocalDateTime;

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
    private String message;

    private LocalDateTime createdAt;

    private Boolean isRead;

    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @OneToOne
    private User fromUser;

    @ManyToOne(cascade = {DETACH, REFRESH, MERGE})
    private User user;

    @OneToOne(cascade = {DETACH, REFRESH, MERGE})
    private SubTask subTask;

    @OneToOne(cascade = {DETACH, REFRESH, MERGE})
    private Card card;

    @OneToOne(cascade = {DETACH, REFRESH, MERGE})
    private kg.peaksoft.taskTrackerb6.db.model.Column column;

    @OneToOne(cascade = {DETACH, REFRESH, MERGE})
    private Estimation estimation;

    @OneToOne(cascade = {DETACH, REFRESH, MERGE})
    private Board board;

}
