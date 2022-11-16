package kg.peaksoft.taskTrackerb6.db.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
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
    @SequenceGenerator(name = "workspace_gen", sequenceName = "workspace_seq", allocationSize = 1, initialValue = 2)
    private Long id;

    private String name;

    private Boolean isFavorite = false;

    @ManyToMany(cascade = {ALL})
    private List<Card> allIssues;

    @ManyToOne(cascade = {DETACH, MERGE, REFRESH})
    private User lead;

    @OneToMany(cascade = {ALL}, mappedBy = "workspace")
    private List<UserWorkSpace> userWorkSpaces;

    @OneToMany(cascade = {ALL}, mappedBy = "workspace")
    private List<Board> boards;

    public Workspace(String name, boolean isFavorite) {
        this.name = name;
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

    public void addCard(Card card) {
        if (allIssues == null) {
            allIssues = new ArrayList<>();
        }
        allIssues.add(card);
    }
}
