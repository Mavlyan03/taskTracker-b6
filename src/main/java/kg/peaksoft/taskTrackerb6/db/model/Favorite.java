package kg.peaksoft.taskTrackerb6.db.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.CascadeType.*;

@Entity
@Table(name = "favorites")
@Getter
@Setter
@NoArgsConstructor
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "favorite_gen")
    @SequenceGenerator(name = "favorite_gen", sequenceName = "favorite_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(cascade = {DETACH, REFRESH, MERGE})
    private User statusChangedUser;

    @OneToOne(cascade = {DETACH, REFRESH, MERGE})
    private Workspace workspace;

    @OneToOne(cascade = {DETACH, REFRESH, MERGE})
    private Board board;

    public Favorite(User user, Workspace workspace) {
        this.statusChangedUser = user;
        this.workspace = workspace;
    }

    public Favorite(User user, Board board) {
        this.statusChangedUser = user;
        this.board = board;
    }
}
