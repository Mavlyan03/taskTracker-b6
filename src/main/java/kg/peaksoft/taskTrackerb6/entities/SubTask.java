package kg.peaksoft.taskTrackerb6.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
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
    @ManyToMany(cascade = {
            CascadeType.DETACH,
            CascadeType.REFRESH,
            CascadeType.MERGE,
            CascadeType.PERSIST}, mappedBy = "workspaces")
    private List<User> userCards;
    @OneToOne(cascade = {
            CascadeType.DETACH,
            CascadeType.REFRESH,
            CascadeType.MERGE,
            CascadeType.PERSIST})
    private Estimation estimation;
    @ManyToOne(cascade = {
            CascadeType.DETACH,
            CascadeType.REFRESH,
            CascadeType.MERGE,
            CascadeType.PERSIST})
    private Checklist checklist;

}
