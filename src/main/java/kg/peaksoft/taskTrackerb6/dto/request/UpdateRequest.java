package kg.peaksoft.taskTrackerb6.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateRequest {

    private Long id;
    private String newTitle;
    private String color;
}
