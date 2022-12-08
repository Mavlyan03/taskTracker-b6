package kg.peaksoft.taskTrackerb6.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.peaksoft.taskTrackerb6.db.service.ChecklistService;
import kg.peaksoft.taskTrackerb6.dto.request.ChecklistRequest;
import kg.peaksoft.taskTrackerb6.dto.request.UpdateRequest;
import kg.peaksoft.taskTrackerb6.dto.response.ChecklistResponse;
import kg.peaksoft.taskTrackerb6.dto.response.SimpleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/checklists")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Checklist API", description = "All endpoints of checklist")
public class ChecklistApi {

    private final ChecklistService checklistService;

    @Operation(summary = "Create checklist", description = "Create new checklist")
    @PostMapping("{id}")
    public ChecklistResponse createChecklist(@PathVariable Long id,
                                             @RequestBody ChecklistRequest request){
        return checklistService.createChecklist(id, request);
    }

    @Operation(summary = "Update checklist title", description = "Update checklist title by id")
    @PutMapping()
    public ChecklistResponse updateTitle(@RequestBody UpdateRequest request){
        return checklistService.updateTitle(request);
    }

    @Operation(summary = "Delete checklist", description = "Delete checklist by id")
    @DeleteMapping("{id}")
    public SimpleResponse deleteChecklist(@PathVariable Long id){
        return checklistService.deleteChecklist(id);
    }

    @Operation(summary = "Get all checklists", description = "Get all checklists by card id")
    @GetMapping("{id}")
    public List<ChecklistResponse> findAllChecklistsByCardId(@PathVariable Long id){
        return checklistService.findAllChecklistsByCardId(id);
    }
}
