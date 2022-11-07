package kg.peaksoft.taskTrackerb6.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CardInnerPageResponse {

    private Long id;
    private String title;
    private String description;
    private List<LabelResponse> labelResponses;
    private EstimationResponse estimationResponse;
    private List<MemberResponse> memberResponses;
    private List<ChecklistResponse> checklistResponses;
    private List<CommentResponse> commentResponses;


    
}
