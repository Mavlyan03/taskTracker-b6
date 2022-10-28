package kg.peaksoft.taskTrackerb6.api;

import kg.peaksoft.taskTrackerb6.db.service.ParticipantService;
import kg.peaksoft.taskTrackerb6.dto.response.ParticipantResponse;
import kg.peaksoft.taskTrackerb6.dto.response.SimpleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.util.List;

@RestController
@RequestMapping("api/participants")
@RequiredArgsConstructor
public class ParticipantApi {

    private final ParticipantService participantService;

    @DeleteMapping("{id}/{workspaceId}")
    public SimpleResponse deleteParticipantById(@PathVariable Long id, @PathVariable Long workspaceId) {
        return participantService.deleteParticipantFromWorkspace(id, workspaceId);
    }

    @DeleteMapping("{id}/{boardId}/board")
    public SimpleResponse deleteParticipantFromBoard(@PathVariable Long id, @PathVariable Long boardId) {
        return participantService.deleteParticipantFromBoard(id, boardId);
    }


    @GetMapping("/board/participants/{boardId}")
    public List<ParticipantResponse> getAllParticipantFromBoard(@PathVariable Long boardId) {
        return participantService.getAllParticipantFromBoard(boardId);
    }


    @GetMapping("/{workspaceId}")
    public List<ParticipantResponse> getAllParticipantFromWorkspace(@PathVariable Long workspaceId) {
        return participantService.getAllParticipantFromWorkspace(workspaceId);
    }

    @PostMapping("/invite")
    public SimpleResponse inviteParticipant(@RequestParam String email, @RequestParam String link) throws MessagingException {
        return participantService.inviteParticipant(email,link);
    }
}


