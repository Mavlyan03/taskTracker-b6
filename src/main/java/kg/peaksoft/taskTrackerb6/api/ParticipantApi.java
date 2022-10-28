package kg.peaksoft.taskTrackerb6.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.peaksoft.taskTrackerb6.db.service.ParticipantService;
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
@Tag(name = "Participant Api", description = "All endpoints of participant")
public class ParticipantApi {

    private final ParticipantService participantService;

    @Operation(summary = "Delete participant from workspace", description = "Delete participant by id from workspace")
    @DeleteMapping("workspace/{id}/{workspaceId}")
    public SimpleResponse deleteParticipantById(@PathVariable Long id, @PathVariable Long workspaceId) {
        return participantService.deleteParticipantFromWorkspace(id, workspaceId);
    }

    @Operation(summary = "Delete participant from board", description = "Delete participant by id from board")
    @DeleteMapping("board/{id}/{boardId}")
    public SimpleResponse deleteParticipantFromBoard(@PathVariable Long id, @PathVariable Long boardId) {
        return participantService.deleteParticipantFromBoard(id, boardId);
    }

    @Operation(summary = "Get participants from board", description = "Get all participants from board")
    @GetMapping("board-participants/{id}")
    public List<ParticipantResponse> getAllParticipantFromBoard(@PathVariable Long id) {
        return participantService.getAllParticipantFromBoard(id);
    }

    @Operation(summary = "Get participants from workspace", description = "Get all participants from workspace")
    @GetMapping("workspace-participants/{id}")
    public List<ParticipantResponse> getAllParticipantFromWorkspace(@PathVariable Long id) {
        return participantService.getAllParticipantFromWorkspace(id);
    }

    @Operation(summary = "Invite member", description = "Invite member")
    @PostMapping("/invite")
    public SimpleResponse inviteParticipant(@RequestParam String email, @RequestParam String link) throws MessagingException {
        return participantService.inviteParticipant(email,link);
    }
}


