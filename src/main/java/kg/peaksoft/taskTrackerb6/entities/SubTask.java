package kg.peaksoft.taskTrackerb6.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

import static javax.persistence.CascadeType.*;

@Entity
@Table(name = "subtasks")
@Getter
@Setter
@NoArgsConstructor
public class SubTask {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sub_gen")
    @SequenceGenerator(name = "sub_gen", sequenceName = "sub_seq", allocationSize = 1)
    private Long id;
    private String description;
    private boolean done = false;

    @ManyToMany(cascade = {DETACH, REFRESH, MERGE, PERSIST}, mappedBy = "workspaces")
    private List<User> workspacesUsers;

    @OneToOne(cascade = {DETACH, REFRESH, MERGE, PERSIST})
    private Estimation estimation;

    @ManyToOne(cascade = {DETACH, REFRESH, MERGE, PERSIST})
    private Checklist checklist;

}
