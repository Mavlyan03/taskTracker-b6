package kg.peaksoft.taskTrackerb6.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "workspace")
@Getter
@Setter
@NoArgsConstructor
public class WorkSpace {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "workspace_gen")
    @SequenceGenerator(name = "workspace_gen", sequenceName = "workspace_seq", allocationSize = 1)
    private Long id;
    private String title;
    private boolean isFavourite = false;
    private String photo;
 @ManyToMany(cascade = {
         CascadeType.DETACH,
         CascadeType.REFRESH,
         CascadeType.MERGE,
         CascadeType.PERSIST})
    private List<User> admins;
    @ManyToMany(cascade = {
            CascadeType.DETACH,
            CascadeType.REFRESH,
            CascadeType.MERGE,
            CascadeType.PERSIST},
            mappedBy = "workspaces")
    private List<User> members;
    @ManyToMany(cascade = {
            CascadeType.DETACH,
            CascadeType.REFRESH,
            CascadeType.MERGE,
            CascadeType.PERSIST})
    private List<Card> allIssues;
    @ManyToOne(cascade = {CascadeType.ALL})
    private User lead;

}
