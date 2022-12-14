package kg.peaksoft.taskTrackerb6.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.peaksoft.taskTrackerb6.db.repository.LabelRepository;
import kg.peaksoft.taskTrackerb6.db.service.AddLabelRequest;
import kg.peaksoft.taskTrackerb6.db.service.LabelService;
import kg.peaksoft.taskTrackerb6.dto.request.LabelRequest;
import kg.peaksoft.taskTrackerb6.dto.request.UpdateRequest;
import kg.peaksoft.taskTrackerb6.dto.response.LabelResponse;
import kg.peaksoft.taskTrackerb6.dto.response.SimpleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/labels")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Label API", description = "Label endpoints for Admin")
public class LabelApi {

    private final LabelService labelService;
    private final LabelRepository labelRepository;

    @Operation(summary = "Create new label", description = "Create new label")
    @PostMapping
    public SimpleResponse createLabel(@RequestBody LabelRequest request) {
        return labelService.saveLabel(request);
    }

    @Operation(summary = "Update label", description = "Update label")
    @PutMapping
    public LabelResponse updateLabel(@RequestBody UpdateRequest request) {
        return labelService.updateLabel(request);
    }

    @Operation(summary = "Get label", description = "Get label by id")
    @GetMapping("/{id}")
    public LabelResponse getLabelById(@PathVariable Long id) {
        return labelService.getLabelById(id);
    }

    @Operation(summary = "Get all labels", description = "Get all labels by card id")
    @GetMapping("/card/{id}")
    public List<LabelResponse> getAllLabelsByCardId(@PathVariable Long id) {
        return labelService.getAllLabelsByCardId(id);
    }

    @Operation(summary = "Delete label", description = "Delete label by id")
    @DeleteMapping("/{cardId}")
    public SimpleResponse deleteLabelById(@PathVariable Long cardId,
                                          @RequestParam Long labelId) {
        return labelService.deleteLabel(cardId, labelId);
    }

    @PostMapping("/add")
    public SimpleResponse addLabelToCard(@RequestBody AddLabelRequest addLabelRequest){
        return labelService.addLabelToCard(addLabelRequest);
    }
}