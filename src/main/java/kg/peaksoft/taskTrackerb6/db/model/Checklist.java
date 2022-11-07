package kg.peaksoft.taskTrackerb6.db.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
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
    @SequenceGenerator(name = "checklist_gen", sequenceName = "checklist_seq", allocationSize = 1, initialValue = 2)
    private Long id;

    private String title;

    private int count;

    @OneToMany(cascade = {ALL}, mappedBy = "checklist")
    private List<SubTask> subTasks;

    @ManyToOne(cascade = {DETACH, MERGE, PERSIST, REFRESH})
    private Card card;

    public Checklist(String title, int count) {
        this.title = title;
        this.count = count;
    }

    public void addSubTaskToChecklist(SubTask subTask) {
        if (subTasks == null) {
            subTasks = new ArrayList<>();
        }
        subTasks.add(subTask);
    }
}
