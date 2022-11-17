package kg.peaksoft.taskTrackerb6.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SubTaskRequest {

    private Boolean isDone;
    private String description;
    private List<MemberRequest> memberRequests;
    private EstimationRequest estimationRequest;
}
