package kg.peaksoft.taskTrackerb6.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.bytebuddy.utility.nullability.MaybeNull;

import javax.persistence.*;
import java.util.List;

import static javax.persistence.CascadeType.*;

@Entity
@Table(name = "checklists")
@Getter
@Setter
@NoArgsConstructor
public class Checklist {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "checklist_gen")
    @SequenceGenerator(name = "checklist_gen", sequenceName = "checklist_seq", allocationSize = 1)
    private Long id;

    private String name;

    private int taskTracker;

    @OneToMany(cascade = {ALL}, mappedBy = "checklist")
    private List<SubTask> subTasks;

    @ManyToOne(cascade = {DETACH, MERGE, PERSIST, REFRESH})
    private Card card;
}
