package kg.peaksoft.taskTrackerb6.dto.request;

import kg.peaksoft.taskTrackerb6.enums.LabelsColor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LabelRequest {

    private String description;
    private LabelsColor color;
}
