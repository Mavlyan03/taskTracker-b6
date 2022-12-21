package kg.peaksoft.taskTrackerb6.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.peaksoft.taskTrackerb6.db.service.AttachmentService;
import kg.peaksoft.taskTrackerb6.dto.request.AttachmentRequest;
import kg.peaksoft.taskTrackerb6.dto.response.AttachmentResponse;
import kg.peaksoft.taskTrackerb6.dto.response.SimpleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/attachments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Attachment API", description = "All endpoints of attachment")
public class AttachmentApi {

    private final AttachmentService attachmentService;

    @Operation(summary = "Add attachment to card", description = "Add attachment to card by card id")
    @PostMapping("/{cardId}")
    public AttachmentResponse addAttachmentToCard(@PathVariable Long cardId,
                                                  @RequestBody AttachmentRequest request) {
        return attachmentService.addAttachmentToCard(cardId, request);
    }

    @Operation(summary = "Delete attachment", description = "Delete attachment by id")
    @DeleteMapping("/{id}")
    public SimpleResponse deleteAttachment(@PathVariable Long id) {
        return attachmentService.deleteAttachment(id);
    }
}
