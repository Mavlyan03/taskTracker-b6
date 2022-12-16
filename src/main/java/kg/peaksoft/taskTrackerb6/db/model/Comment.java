package kg.peaksoft.taskTrackerb6.db.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.persistence.Column;

import static javax.persistence.CascadeType.*;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comment_gen")
    @SequenceGenerator(name = "comment_gen", sequenceName = "comment_seq", allocationSize = 1, initialValue = 2)
    private Long id;

    @Column(length = 10000)
    private String text;

    private String createdAt;

    private Boolean isMyComment;

    @ManyToOne(cascade = {DETACH, MERGE, REFRESH})
    private User user;

    @ManyToOne(cascade = {DETACH, REFRESH, MERGE})
    private Card card;

    public Comment(String text, String createdAt, Boolean isMyComment, User creator) {
        this.text = text;
        this.createdAt = createdAt;
        this.isMyComment = isMyComment;
        this.user = creator;
    }

    public Comment(String text, String addCommentDate, User user) {
        this.text = text;
        this.createdAt = addCommentDate;
        this.user = user;
    }
}
