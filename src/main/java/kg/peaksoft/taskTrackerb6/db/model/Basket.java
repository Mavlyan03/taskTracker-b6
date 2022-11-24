package kg.peaksoft.taskTrackerb6.db.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

import static javax.persistence.CascadeType.*;

@Entity
@Table(name = "baskets")
@Getter
@Setter
@NoArgsConstructor
public class Basket {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "basket_gen")
    @SequenceGenerator(name = "basket_gen", sequenceName = "basket_seq", allocationSize = 1, initialValue = 2)
    private Long id;

    private LocalDate archiveDate;

    @OneToOne(cascade = ALL)
    private Board board;

    @OneToOne(cascade = ALL)
    private Card card;

    @OneToOne(cascade = ALL)
    private Column column;
}
