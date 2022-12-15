package kg.peaksoft.taskTrackerb6.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddLabelRequest {

    private Long cardId;
    private Long labelId;
}
