package kg.peaksoft.taskTrackerb6.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminProfileResponse {

    private Long userId;
    private String photo;
    private String firstName;
    private String lastName;
    private String email;
    private List<ProjectResponse> projectResponses;


}
