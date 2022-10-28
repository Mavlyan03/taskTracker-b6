package kg.peaksoft.taskTrackerb6.dto.request;

import kg.peaksoft.taskTrackerb6.validations.PasswordValid;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ResetPasswordRequest {

    private Long userId;

    @NotNull
    @NotBlank
    @PasswordValid
    private String newPassword;

}
