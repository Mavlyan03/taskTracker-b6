package kg.peaksoft.taskTrackerb6.dto.request;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AdminProfileRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String image;

}
