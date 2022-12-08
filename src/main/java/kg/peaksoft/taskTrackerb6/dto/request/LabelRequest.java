package kg.peaksoft.taskTrackerb6.dto.request;

import kg.peaksoft.taskTrackerb6.enums.LabelsColor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LabelRequest {

    private String description;
    private LabelsColor color;
}
