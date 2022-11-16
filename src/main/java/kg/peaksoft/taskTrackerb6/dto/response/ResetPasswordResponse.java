package kg.peaksoft.taskTrackerb6.dto.response;

import kg.peaksoft.taskTrackerb6.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ResetPasswordResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Role role;
    private String jwt;
    private String message;
}
