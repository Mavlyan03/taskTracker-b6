package kg.peaksoft.taskTrackerb6.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class WorkspaceResponse {

    private Long id;
    private String name;
    private CreatorResponse lead;
//    private Long leadId;
//    private String firstName;
//    private String lastName;
//    private String image;
    private Boolean action;

    public WorkspaceResponse(Long id, String name, Boolean action) {
        this.id = id;
        this.name = name;
        this.action = action;
    }

//    public WorkspaceResponse(Long id, String name, Long leadId, String firstName, String lastName, String image, Boolean action) {
//        this.id = id;
//        this.name = name;
//        this.leadId = leadId;
//        this.firstName = firstName;
//        this.lastName = lastName;
//        this.image = image;
//        this.action = action;
//    }
}
