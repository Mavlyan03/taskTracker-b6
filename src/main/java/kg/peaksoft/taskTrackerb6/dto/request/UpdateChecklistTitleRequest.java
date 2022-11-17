package kg.peaksoft.taskTrackerb6.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateChecklistTitleRequest {

    private Long checklistId;
    private String newTitle;
}
