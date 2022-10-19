package kg.peaksoft.taskTrackerb6.db.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

import static javax.persistence.CascadeType.*;

@Entity
@Table(name = "lines")
@Getter
@Setter
@NoArgsConstructor
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "line_gen")
    @SequenceGenerator(name = "line_gen", sequenceName = "line_seq", allocationSize = 1, initialValue = 2)
    private Long id;

    private String title;

    @OneToMany(cascade = ALL, mappedBy = "line")
    private List<Card> cards;

    @ManyToOne(cascade = {DETACH, REFRESH, MERGE, PERSIST})
    private Board board;
}
