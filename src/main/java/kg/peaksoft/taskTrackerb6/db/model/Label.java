package kg.peaksoft.taskTrackerb6.db.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.persistence.Column;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.*;

@Entity
@Table(name = "labels")
@Getter
@Setter
@NoArgsConstructor
public class Label {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "label_gen")
    @SequenceGenerator(name = "label_gen", sequenceName = "label_seq", allocationSize = 1, initialValue = 2)
    private Long id;

    @Column(length = 10000)
    private String description;

    private String color;

    @ManyToMany(cascade = {DETACH, REFRESH, MERGE}, mappedBy = "labels")
    private List<Card> cards;

    @JsonCreator
    public Label(@JsonProperty("description") String description,@JsonProperty("color") String color) {
        this.description = description;
        this.color = color;
    }

    public void addCard(Card card) {
        if (cards == null) {
            cards = new ArrayList<>();
        }
        cards.add(card);
    }
}
