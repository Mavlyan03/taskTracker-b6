package kg.peaksoft.taskTrackerb6.db.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.*;

@Entity
@Table(name = "columns")
@Getter
@Setter
@NoArgsConstructor
public class Column {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "column_gen")
    @SequenceGenerator(name = "column_gen", sequenceName = "column_seq", allocationSize = 1, initialValue = 2)
    private Long id;

    private String title;

    private Boolean isArchive = false;

    @OneToMany(cascade = ALL, mappedBy = "column")
    private List<Card> cards;

    @ManyToOne(cascade = {DETACH, REFRESH, MERGE, PERSIST})
    private Board board;

    public void addCard(Card card) {
        if (cards == null) {
            cards = new ArrayList<>();
        }
        cards.add(card);
    }
}