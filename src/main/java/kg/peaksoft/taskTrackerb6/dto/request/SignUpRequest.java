package kg.peaksoft.taskTrackerb6.dto.request;

import kg.peaksoft.taskTrackerb6.validation.PasswordValid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Valid
public class SignUpRequest {
    @NotNull
    @NotBlank
    private String firstName;
    @NotNull
    @NotBlank
    private String lastName;
    @Email
    private String email;
    @NotNull
    @NotBlank
    @PasswordValid
    private String password;

}
