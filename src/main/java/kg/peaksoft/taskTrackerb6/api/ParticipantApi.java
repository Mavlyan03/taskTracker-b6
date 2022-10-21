package kg.peaksoft.taskTrackerb6.api;

import kg.peaksoft.taskTrackerb6.db.model.User;
import kg.peaksoft.taskTrackerb6.db.service.ParticipantService;
import kg.peaksoft.taskTrackerb6.dto.response.SimpleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/participants")
@RequiredArgsConstructor
public class ParticipantApi {

    private final ParticipantService participantService;

    @DeleteMapping("${id}/{workspaceId}")
    public SimpleResponse deleteParticipantById(@PathVariable Long id, @PathVariable Long workspaceId) {
        return participantService.deleteParticipantById(id, workspaceId);
    }

    @DeleteMapping("${id}/{boardId}/board")
    public SimpleResponse deleteParticipantFromBoard(@PathVariable Long id, @PathVariable Long boardId) {
        return participantService.deleteParticipantFromBoard(id, boardId);
    }

    @GetMapping("{boardId}")
    public User getAllParticipantFromBoard(@PathVariable Long boardId) {
        return participantService.getAllParticipantFromBoard(boardId);
    }

    @GetMapping("{workspaceId}")
    public User getAllParticipantFromWorkspace(@PathVariable Long workspaceId) {
        return participantService.getAllParticipantFromWorkspace(workspaceId);
    }

}
