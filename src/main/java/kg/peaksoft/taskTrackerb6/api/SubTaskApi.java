package kg.peaksoft.taskTrackerb6.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.peaksoft.taskTrackerb6.db.service.SubTaskService;
import kg.peaksoft.taskTrackerb6.dto.request.SubTaskRequest;
import kg.peaksoft.taskTrackerb6.dto.response.SubTaskResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/subtasks")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Subtask API", description = "All endpoints of subtask")
public class SubTaskApi {

    private final SubTaskService subTaskService;

    @Operation(summary = "Create subtask", description = "Create new subtask")
    @PostMapping("{id}")
    public SubTaskResponse createSubTask(@PathVariable Long id,
                                         @RequestBody SubTaskRequest request){
        return subTaskService.createSubTask(id, request);
    }
}
