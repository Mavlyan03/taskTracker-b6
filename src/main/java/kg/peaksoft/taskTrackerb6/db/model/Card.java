package kg.peaksoft.taskTrackerb6.db.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import javax.persistence.*;
import javax.persistence.Column;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


import static javax.persistence.CascadeType.*;

@Entity
@Table(name = "cards")
@Getter
@Setter
@NoArgsConstructor
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "card_gen")
    @SequenceGenerator(name = "card_gen", sequenceName = "card_seq", allocationSize = 1, initialValue = 2)
    private Long id;

    private String title;

    @Column(length = 10000)
    private String description;

    private Boolean isArchive = false;

    private LocalDate createdAt;

    @ManyToOne(cascade = {DETACH, REFRESH, MERGE, PERSIST})
    private User creator;

    @OneToOne
    private User movedUser;

    @ManyToMany(cascade = {DETACH, REFRESH, MERGE, PERSIST})
    private List<User> members;

    @OneToMany(cascade = {ALL}, mappedBy = "card")
    private List<Checklist> checklists;

    @OneToOne(cascade = {ALL}, mappedBy = "card")
    private Estimation estimation;

    @ManyToOne(cascade = {DETACH, REFRESH, MERGE, PERSIST})
    private kg.peaksoft.taskTrackerb6.db.model.Column                                                       column;

    @OneToMany(cascade = {ALL}, mappedBy = "card")
    private List<Label> labels;

    @OneToMany(cascade = {ALL}, mappedBy = "card")
    private List<Comment> comments;

    @OneToMany(cascade = {ALL}, mappedBy = "card")
    private List<Attachment> attachments;

    @ManyToOne(cascade = {DETACH, REFRESH, MERGE, PERSIST})
    private Board board;

    public Card(String title, String description, Boolean isArchive, LocalDate createdAt, User creator) {
        this.title = title;
        this.description = description;
        this.isArchive = isArchive;
        this.createdAt = createdAt;
        this.creator = creator;
    }

    public Card(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public void addLabel(Label label) {
        if (labels == null) {
            labels = new ArrayList<>();
        }
        labels.add(label);
    }

    public void addMember(User user) {
        if (members == null) {
            members = new ArrayList<>();
        }
        members.add(user);
    }

    public void addChecklist(Checklist checklist) {
        if (checklists == null) {
            checklists = new ArrayList<>();
        }
        checklists.add(checklist);
    }

    public void addComment(Comment comment) {
        if (comments == null) {
            comments = new ArrayList<>();
        }
        comments.add(comment);
    }
}
