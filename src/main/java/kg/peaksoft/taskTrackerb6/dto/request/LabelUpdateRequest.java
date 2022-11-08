package kg.peaksoft.taskTrackerb6.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LabelUpdateRequest {

    private String description;
}