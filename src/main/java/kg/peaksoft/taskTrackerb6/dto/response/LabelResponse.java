package kg.peaksoft.taskTrackerb6.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import kg.peaksoft.taskTrackerb6.db.model.Label;
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


    public LabelResponse(Label label) {
        this.id = label.getId();
        this.description = label.getDescription();
        this.color = label.getColor();
    }

}
