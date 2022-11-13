package kg.peaksoft.taskTrackerb6.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import kg.peaksoft.taskTrackerb6.db.service.ChecklistService;
import kg.peaksoft.taskTrackerb6.dto.request.ChecklistRequest;
import kg.peaksoft.taskTrackerb6.dto.request.UpdateChecklistTitleRequest;
import kg.peaksoft.taskTrackerb6.dto.response.CardInnerPageResponse;
import kg.peaksoft.taskTrackerb6.dto.response.ChecklistResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/checklists")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Checklist API", description = "All endpoints of checklist")
public class ChecklistApi {

    private final ChecklistService checklistService;

    @PostMapping("{id}")
    public ChecklistResponse createChecklist(@PathVariable Long id,
                                             @RequestBody ChecklistRequest request){
        return checklistService.createChecklist(id, request);
    }

    @PutMapping()
    public ChecklistResponse updateTitle(@RequestBody UpdateChecklistTitleRequest request){
        return checklistService.updateTitle(request);
    }
}
