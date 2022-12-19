package kg.peaksoft.taskTrackerb6.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.peaksoft.taskTrackerb6.db.service.EstimationService;
import kg.peaksoft.taskTrackerb6.dto.request.EstimationRequest;
import kg.peaksoft.taskTrackerb6.dto.response.EstimationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/estimation")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Estimation API", description = "All endpoints of estimation")
public class EstimationApi {

    private final EstimationService estimationService;

    @Operation(summary = "Add estimation to card", description = "Add estimation to card")
    @PostMapping("/{cardId}")
    public EstimationResponse addEstimationToCard(@PathVariable Long cardId,
                                                  @RequestBody EstimationRequest request) {
        return estimationService.addEstimationToCard(cardId, request);
    }

    @Operation(summary = "Update estimation", description = "Update esmitation by id")
    @PutMapping("{id}")
    public EstimationResponse updateEstimation(@PathVariable Long id,
                                               @RequestBody EstimationRequest request) {
        return estimationService.updateEstimation(id, request);
    }
}
