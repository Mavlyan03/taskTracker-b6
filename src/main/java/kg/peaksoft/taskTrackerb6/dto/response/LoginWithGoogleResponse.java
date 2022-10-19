package kg.peaksoft.taskTrackerb6.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginWithGoogleResponse {

    private String message;
    private String  token;
}
