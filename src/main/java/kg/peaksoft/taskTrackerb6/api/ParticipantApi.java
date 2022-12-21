package kg.peaksoft.taskTrackerb6.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.peaksoft.taskTrackerb6.db.service.ParticipantService;
import kg.peaksoft.taskTrackerb6.dto.request.InviteRequest;
import kg.peaksoft.taskTrackerb6.dto.response.ParticipantResponse;
import kg.peaksoft.taskTrackerb6.dto.response.SimpleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.util.List;

@RestController
@RequestMapping("api/participant")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Participant API", description = "All endpoints of participant")
public class ParticipantApi {

    private final ParticipantService participantService;

    @Operation(summary = "Delete participant from workspace", description = "Delete participant by id from workspace")
    @DeleteMapping("/workspace/{userId}/{workspaceId}")
    public SimpleResponse deleteParticipantById(@PathVariable Long userId,
                                                @PathVariable Long workspaceId) {
        return participantService.deleteParticipantFromWorkspace(userId, workspaceId);
    }

    @Operation(summary = "Delete participant from board", description = "Delete participant by id from board")
    @DeleteMapping("/board/{userId}/{boardId}")
    public SimpleResponse deleteParticipantFromBoard(@PathVariable Long userId,
                                                     @PathVariable Long boardId) {
        return participantService.deleteParticipantFromBoard(userId, boardId);
    }

    @Operation(summary = "Get participants from board", description = "Get all participants from board")
    @GetMapping("/board-participants/{id}")
    public List<ParticipantResponse> getAllParticipantFromBoard(@PathVariable Long id) {
        return participantService.getAllParticipantFromBoard(id);
    }

    @Operation(summary = "Get participants from workspace", description = "Get all participants from workspace")
    @GetMapping("/workspace-participants/{id}")
    public List<ParticipantResponse> getAllParticipantFromWorkspace(@PathVariable Long id) {
        return participantService.getAllParticipantFromWorkspace(id);
    }

    @Operation(summary = "Invite new participant to board", description = "Invite new participant to board")
    @PostMapping("/board-invite")
    public SimpleResponse inviteParticipant(@RequestBody InviteRequest request) throws MessagingException {
        return participantService.inviteNewParticipantToBoard(request);
    }

    @Operation(summary = "Invite new participant to workspace", description = "Invite new participant to workspace")
    @PostMapping("/invite-workspace")
    public SimpleResponse inviteNewParticipantToWorkspace(@RequestBody InviteRequest request) throws MessagingException {
        return participantService.inviteNewParticipantToWorkspace(request);
    }
}


