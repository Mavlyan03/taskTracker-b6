package kg.peaksoft.taskTrackerb6.db.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.*;

import static javax.persistence.CascadeType.*;

@Entity
@Table(name = "subtasks")
@Getter
@Setter
@NoArgsConstructor
public class SubTask {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sub_task_gen")
    @SequenceGenerator(name = "sub_task_gen", sequenceName = "sub_task_seq", allocationSize = 1, initialValue = 2)
    private Long id;

    @Column(length = 10000)
    private String description;

    private Boolean isDone = false;

    @ManyToOne(cascade = {DETACH, REFRESH, MERGE})
    private Checklist checklist;

    public SubTask(String description, Boolean isDone) {
        this.description = description;
        this.isDone = isDone;
    }
}
