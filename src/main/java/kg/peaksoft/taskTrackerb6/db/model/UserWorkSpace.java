package kg.peaksoft.taskTrackerb6.db.model;

import kg.peaksoft.taskTrackerb6.enums.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

import static javax.persistence.CascadeType.*;
import static javax.persistence.CascadeType.MERGE;

@Entity
@Table(name = "user_workspace_roles")
@Getter
@Setter
@NoArgsConstructor
public class UserWorkSpace {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_workspace_roles_gen")
    @SequenceGenerator(name = "user_workspace_roles_gen", sequenceName = "user_workspace_roles_seq",allocationSize = 1, initialValue = 2)
    private Long id;

    @ManyToOne(cascade = {REFRESH, DETACH, PERSIST, MERGE})
    private User user;

    @ManyToOne(cascade = {REFRESH, DETACH, PERSIST, MERGE})
    private Workspace workspace;

    @Enumerated(EnumType.STRING)
    private Role role;
}
