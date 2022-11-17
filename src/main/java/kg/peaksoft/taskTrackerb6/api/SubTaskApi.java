package kg.peaksoft.taskTrackerb6.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.peaksoft.taskTrackerb6.db.service.SubTaskService;
import kg.peaksoft.taskTrackerb6.dto.request.SubTaskRequest;
import kg.peaksoft.taskTrackerb6.dto.response.SimpleResponse;
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

    @Operation(summary = "Update subtask description", description = "Update subtask description by id")
    @PutMapping("{id}")
    public SubTaskResponse updateDescription(@PathVariable Long id,
                                             @RequestBody SubTaskRequest request){
        return subTaskService.updateDescription(id, request);
    }

    @Operation(summary = "Delete subtask", description = "Delete subtask by id")
    @DeleteMapping("{id}")
    public SimpleResponse deleteSubTask(@PathVariable Long id){
        return subTaskService.deleteSubTask(id);
    }

    @Operation(summary = "Complete", description = "Add this subtask to completed")
    @PutMapping("complete/{id}")
    public SubTaskResponse addToCompleted(@PathVariable Long id){
        return subTaskService.addToCompleted(id);
    }

    @Operation(summary = "Uncheck", description = "Uncheck the subtask")
    @PutMapping("subtask/{id}")
    public SubTaskResponse uncheck(@PathVariable Long id){
        return subTaskService.uncheck(id);
    }
}
