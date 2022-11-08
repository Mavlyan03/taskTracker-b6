package kg.peaksoft.taskTrackerb6.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import kg.peaksoft.taskTrackerb6.db.service.LabelService;
import kg.peaksoft.taskTrackerb6.dto.request.LabelUpdateRequest;
import kg.peaksoft.taskTrackerb6.dto.response.SimpleResponse;

@RestController
@RequestMapping("api/labels")
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('ADMIN')")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Label API", description = "Label endpoints for Admin")
public class LabelController {

    private final LabelService labelService;

    @Operation(summary = "Description update method",
            description = "Update description by label id")
    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    public SimpleResponse updateDescription(@PathVariable Long id,
                                            @RequestBody LabelUpdateRequest labelUpdateRequest) {
        return labelService.updateLabel(id, labelUpdateRequest);
    }
}