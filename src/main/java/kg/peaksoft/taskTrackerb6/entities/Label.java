package kg.peaksoft.taskTrackerb6.entities;

import kg.peaksoft.taskTrackerb6.enums.LabelsColour;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "labels")
@Getter
@Setter
@NoArgsConstructor
public class Label {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "label_gen")
    @SequenceGenerator(name = "label_gen",sequenceName = "label_seq",allocationSize = 1)
    private Long id;
    private String description;
    private LabelsColour labelsColour;
    @ManyToOne(cascade = {
            CascadeType.DETACH,
            CascadeType.REFRESH,
            CascadeType.MERGE,
            CascadeType.PERSIST})
    private Card card;

}
