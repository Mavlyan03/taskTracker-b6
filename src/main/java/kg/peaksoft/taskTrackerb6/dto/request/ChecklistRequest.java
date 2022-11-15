package kg.peaksoft.taskTrackerb6.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ChecklistRequest {

    private String title;
//    private int count;
    private List<SubTaskRequest> subTaskRequests;
}
