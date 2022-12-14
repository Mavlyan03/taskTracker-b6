package kg.peaksoft.taskTrackerb6.db.model;

import kg.peaksoft.taskTrackerb6.dto.request.BoardRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.*;

@Entity
@Table(name = "boards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "board_gen")
    @SequenceGenerator(name = "board_gen", sequenceName = "board_seq", allocationSize = 1, initialValue = 3)
    private Long id;

    private String title;

    private Boolean isFavorite = false;

    private String background;

    private LocalDateTime createdAt;

    public Board(BoardRequest boardRequest) {
        this.title = boardRequest.getTitle();
        this.background = boardRequest.getBackground();
    }

    @OneToMany(cascade = ALL, mappedBy = "board")
    private List<Column> columns;

    @ManyToMany(cascade = {DETACH, REFRESH, MERGE}, mappedBy = "boards")
    private List<User> members;

    @ManyToOne(cascade = {DETACH, REFRESH, MERGE, PERSIST})
    private Workspace workspace;

    @OneToOne(cascade = ALL, mappedBy = "board")
    private Favorite favorite;

    public void addColumn(Column column){
        if (columns== null){
            columns = new ArrayList<>();
        }
        columns.add(column);
    }

    public void addMember(User user) {
        if (members == null) {
            members = new ArrayList<>();
        }
        members.add(user);
    }
}
