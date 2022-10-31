package kg.peaksoft.taskTrackerb6.dto.request;

import kg.peaksoft.taskTrackerb6.db.model.Label;
import kg.peaksoft.taskTrackerb6.dto.response.ChecklistResponse;
import kg.peaksoft.taskTrackerb6.dto.response.CommentResponse;
import kg.peaksoft.taskTrackerb6.dto.response.EstimationResponse;
import kg.peaksoft.taskTrackerb6.dto.response.MemberResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardRequest {

    private String title;
    private String description;
    private List<Label> labels;
    private EstimationResponse estimationResponse;
    private List<MemberResponse> memberResponses;
    private ChecklistResponse checklistResponse;
    private List<CommentResponse> commentResponses;
}
