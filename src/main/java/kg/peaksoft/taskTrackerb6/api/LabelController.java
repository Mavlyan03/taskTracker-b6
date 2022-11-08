package kg.peaksoft.taskTrackerb6.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.peaksoft.taskTrackerb6.db.service.LabelService;
import kg.peaksoft.taskTrackerb6.dto.request.LabelRequest;
import kg.peaksoft.taskTrackerb6.dto.response.LabelResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/labels")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ADMIN')")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Label API", description = "Label endpoints for Admin")
public class LabelController {

    private final LabelService labelService;

    @Operation(summary = "Update abel",
            description = "Update label by id")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/update/{id}")
    public LabelResponse update(@PathVariable Long id,
                                @RequestBody LabelRequest labelRequest) {
        return labelService.updateLabel(id, labelRequest);
    }

    @Operation(summary = "Get label", description = "Get label by id")
    @GetMapping("{id}")
    public LabelResponse getById(@PathVariable Long id) {
        return labelService.getLabelById(id);
    }

    @Operation(summary = "Get all labels", description = "Get all labels by card id")
    @GetMapping("list/{id}")
    public List<LabelResponse> getAllLabelsByCardId(@PathVariable Long id) {
        return labelService.getAllLabelsByCardId(id);
    }
}