package kg.peaksoft.taskTrackerb6.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.persistence.Column;
import java.util.List;


import static javax.persistence.CascadeType.*;
import static javax.persistence.CascadeType.PERSIST;

@Entity
@Table(name = "cards")
@Getter
@Setter
@NoArgsConstructor
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "card_gen")
    @SequenceGenerator(name = "card_gen", sequenceName = "card_seq", allocationSize = 1)
    private Long id;

    private String title;

    @Column(length = 10000)
    private String description;

    private boolean idArchive = false;

    @ManyToMany(cascade = {DETACH, REFRESH, MERGE, PERSIST})
    private List<User> members;

    @OneToMany(cascade = {ALL}, mappedBy = "card")
    private List<Checklist> checklists;

    @OneToOne(cascade = {ALL})
    private Estimation estimation;

    @ManyToOne(cascade = {DETACH, REFRESH, MERGE, PERSIST})
    private Line line;

    @OneToMany(cascade = {ALL}, mappedBy = "card")
    private List<Label> labels;

    @OneToMany(cascade = {ALL}, mappedBy = "card")
    private List<Comment> comments;

    @OneToMany(cascade = {ALL}, mappedBy = "card")
    private List<Attachment> attachments;

    @ManyToOne(cascade = {DETACH, REFRESH, MERGE, PERSIST})
    private Board board;
}
