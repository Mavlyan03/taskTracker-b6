package kg.peaksoft.taskTrackerb6.entities;

import kg.peaksoft.taskTrackerb6.enums.Role;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.persistence.Column;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static javax.persistence.CascadeType.*;
import static javax.persistence.CascadeType.ALL;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_gen")
    @SequenceGenerator(name = "user_gen", sequenceName = "user_seq", allocationSize = 1, initialValue = 4)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private String photoLink;

    private String email;

    private String password;

    @OneToMany(cascade = {ALL}, mappedBy = "user")
    private List<Notification> notifications;

    @ManyToMany(cascade = {DETACH, REFRESH, MERGE, PERSIST}, fetch = FetchType.EAGER)
    private List<Workspace> workspaces;

    @OneToMany(cascade = {DETACH, REFRESH, MERGE, PERSIST})
    private List<Workspace> leadWorkspaces;

    @ManyToMany(cascade = {ALL})
    private List<Board> boards;

    @Enumerated(EnumType.STRING)
    private Role role;

    public User(String firstName, String lastName, String photoLink, String email, String password, Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.photoLink = photoLink;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public void addWorkspace(Workspace workspace) {
        if (workspaces == null) {
            workspaces = new ArrayList<>();
        }
        workspaces.add(workspace);
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(role);
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
