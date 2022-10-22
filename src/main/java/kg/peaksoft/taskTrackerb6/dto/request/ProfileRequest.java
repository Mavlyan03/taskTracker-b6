package kg.peaksoft.taskTrackerb6.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;

@Getter
@Setter
public class ProfileRequest {

    private String firstName;
    private String lastName;
    private String photoLink;
    private String email;
    private String password;
}
