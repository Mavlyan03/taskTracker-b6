package kg.peaksoft.taskTrackerb6.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CardRequest {

    private Long columnId;
    private String title;
    private String description;
//    private List<LabelRequest> labelRequests;
//    private EstimationRequest estimationRequest;
//    private List<MemberRequest> memberRequests;
//    private List<ChecklistRequest> checklistRequests;
//    private List<CommentRequest> commentRequests;

}
