package kg.peaksoft.taskTrackerb6.dto.response;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SubTaskResponse {

    private Long id;
    private String description;
    private Boolean isDone;

    public SubTaskResponse(Long id, String description, Boolean isDone) {
        this.id = id;
        this.description = description;
        this.isDone = isDone;
    }
}
