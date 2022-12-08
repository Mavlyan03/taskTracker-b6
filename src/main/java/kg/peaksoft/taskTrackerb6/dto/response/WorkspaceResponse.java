package kg.peaksoft.taskTrackerb6.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceResponse {

    private Long id;
    private String name;
    private CreatorResponse lead;
    private Boolean isFavorite;
}
