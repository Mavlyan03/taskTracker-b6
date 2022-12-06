package kg.peaksoft.taskTrackerb6.dto.response;

import kg.peaksoft.taskTrackerb6.db.model.Workspace;
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
    private Boolean action;
//    private Long leadId;
//    private String firstName;
//    private String lastName;
//    private String image;

    public WorkspaceResponse(Workspace workspace) {
        this.id = workspace.getId();
        this.name = workspace.getName();
        this.action = workspace.getIsFavorite();
//        this.leadId = workspace.getLead().getId();
//        this.firstName = workspace.getLead().getFirstName();
//        this.lastName = workspace.getLead().getLastName();
//        this.image = workspace.getLead().getImage();
    }
}
