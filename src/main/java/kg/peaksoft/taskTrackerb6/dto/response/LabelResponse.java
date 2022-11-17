package kg.peaksoft.taskTrackerb6.dto.response;

import kg.peaksoft.taskTrackerb6.enums.LabelsColor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LabelResponse {

    private Long id;
    private String description;
    private LabelsColor color;

}
