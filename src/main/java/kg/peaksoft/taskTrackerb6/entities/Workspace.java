package kg.peaksoft.taskTrackerb6.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
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
    @SequenceGenerator(name = "workspace_gen", sequenceName = "workspace_seq", allocationSize = 1)
    private Long id;

    private String name;

    private boolean isFavourite = false;

    private String photo;

    @ManyToMany(cascade = {DETACH, REFRESH, MERGE, PERSIST}, mappedBy = "workspaces")
    private List<User> members;

    @ManyToMany(cascade = {DETACH, REFRESH, MERGE, PERSIST})
    private List<Card> allIssues;

    @ManyToOne(cascade = {ALL})
    private User lead;


}
