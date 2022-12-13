package kg.peaksoft.taskTrackerb6.db.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.*;

@Entity
@Table(name = "workspaces")
@Getter
@Setter
@NoArgsConstructor
public class Workspace {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "workspace_gen")
    @SequenceGenerator(name = "workspace_gen", sequenceName = "workspace_seq", allocationSize = 1, initialValue = 4)
    private Long id;

    private String name;

    private Boolean isFavorite = false;

    private LocalDateTime createdAt;

    @ManyToOne(cascade = {DETACH, MERGE, REFRESH, PERSIST})
    private User lead;

    @OneToMany(cascade = {ALL}, mappedBy = "workspace")
    private List<UserWorkSpace> userWorkSpaces;

    @OneToMany(cascade = {ALL}, mappedBy = "workspace")
    private List<Board> boards;

    @OneToOne(cascade = ALL, mappedBy = "workspace")
    private Favorite favorite;


    public Workspace(Long id, String name, Boolean isFavorite) {
        this.id = id;
        this.name = name;
        this.isFavorite = isFavorite;
    }

    public Workspace(Long id, String name, User lead, Boolean isFavorite) {
        this.id = id;
        this.name = name;
        this.lead = lead;
        this.isFavorite = isFavorite;
    }

    public void addBoard(Board board) {
        if (boards == null) {
            boards = new ArrayList<>();
        }
        boards.add(board);
    }

    public void addUserWorkSpace(UserWorkSpace userWorkSpace) {
        if (userWorkSpaces == null) {
            userWorkSpaces = new ArrayList<>();
        }
        userWorkSpaces.add(userWorkSpace);
    }
}
