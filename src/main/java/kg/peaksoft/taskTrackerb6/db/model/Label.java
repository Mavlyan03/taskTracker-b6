package kg.peaksoft.taskTrackerb6.db.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import kg.peaksoft.taskTrackerb6.enums.LabelsColor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.persistence.Column;

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

    @Enumerated(EnumType.STRING)
    private LabelsColor color;

    @ManyToOne(cascade = {DETACH, REFRESH, MERGE, PERSIST})
    private Card card;

    @JsonCreator
    public Label(@JsonProperty("description") String description,@JsonProperty("color") LabelsColor color) {
        this.description = description;
        this.color = color;
    }

}
