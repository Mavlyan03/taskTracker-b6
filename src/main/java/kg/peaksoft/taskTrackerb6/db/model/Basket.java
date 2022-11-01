package kg.peaksoft.taskTrackerb6.db.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

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

    @OneToOne(cascade = CascadeType.ALL)
    private Board board;

    @OneToOne(cascade = CascadeType.ALL)
    private Card card;

    @OneToOne(cascade = CascadeType.ALL)
    private Column column;
}
