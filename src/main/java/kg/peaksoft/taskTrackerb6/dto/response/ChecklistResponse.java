package kg.peaksoft.taskTrackerb6.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ChecklistResponse {

    private Long id;
    private String title;
    private int countOfCompletedSubTask;
    private int countOfSubTasks;
    private int count;
    private List<SubTaskResponse> subTaskResponses;
}
