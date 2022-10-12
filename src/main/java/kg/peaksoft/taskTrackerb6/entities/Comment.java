package kg.peaksoft.taskTrackerb6.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.CascadeType.*;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comment_gen")
    @SequenceGenerator(name = "comment_gen", sequenceName = "comment_seq", allocationSize = 1)
    private Long id;
    private String text;
    private LocalDateTime created;

    @ManyToOne(cascade = {DETACH, MERGE, PERSIST, REFRESH})
    private User user;

    @ManyToOne(cascade = {DETACH, REFRESH, MERGE, PERSIST})
    private Card card;


}
