package kg.peaksoft.taskTrackerb6.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String image;

    public MemberResponse(Long id, String firstName, String lastName, String email, String image) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.image = image;
    }
}
