package kg.peaksoft.taskTrackerb6.db.model;

import kg.peaksoft.taskTrackerb6.dto.request.BoardRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

import static javax.persistence.CascadeType.*;
import static javax.persistence.CascadeType.PERSIST;

@Entity
@Table(name = "boards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "board_gen")
    @SequenceGenerator(name = "board_gen", sequenceName = "board_seq", allocationSize = 1, initialValue = 2)
    private Long id;

    private String title;

    private String photoLink;

    private Boolean isArchive = false;

    private Boolean isFavorite = false;

    private String background;

    public Board(BoardRequest boardRequest) {
        this.title = boardRequest.getTitle();
        this.background = boardRequest.getBackground();
    }

    @OneToMany(cascade = ALL, mappedBy = "board")
    private List<Line> lines;

    @ManyToOne(cascade = {DETACH, REFRESH, MERGE, PERSIST})
    private User creator;

    @ManyToMany(cascade = {DETACH, REFRESH, MERGE, PERSIST}, mappedBy = "boards")
    private List<User> members;

    @ManyToOne(cascade = {DETACH, REFRESH, MERGE, PERSIST})
    private Workspace workspace;
}
