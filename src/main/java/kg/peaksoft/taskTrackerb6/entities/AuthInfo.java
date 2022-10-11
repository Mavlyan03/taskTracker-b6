package kg.peaksoft.taskTrackerb6.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "auth_infos")
@Getter
@Setter
@NoArgsConstructor
public class AuthInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "auth_gen")
    @SequenceGenerator(name = "auth_gen", sequenceName = "auth_seq", allocationSize = 1)
    private Long id;
    private String email;
    private String password;
    @ManyToMany(targetEntity = Role.class,
            cascade = {
                    CascadeType.DETACH,
                    CascadeType.REFRESH,
                    CascadeType.MERGE,
                    CascadeType.PERSIST},
            fetch = FetchType.EAGER)
    @JoinTable(name = "auth_roles",
            joinColumns = @JoinColumn(name = "auth_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles;
}
