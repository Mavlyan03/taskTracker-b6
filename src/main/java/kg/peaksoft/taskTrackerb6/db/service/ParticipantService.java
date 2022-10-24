package kg.peaksoft.taskTrackerb6.db.service;


import kg.peaksoft.taskTrackerb6.db.model.Board;
import kg.peaksoft.taskTrackerb6.db.model.User;
import kg.peaksoft.taskTrackerb6.db.model.UserWorkSpace;
import kg.peaksoft.taskTrackerb6.db.model.Workspace;
import kg.peaksoft.taskTrackerb6.db.repository.*;
import kg.peaksoft.taskTrackerb6.dto.response.ParticipantResponse;
import kg.peaksoft.taskTrackerb6.dto.response.SimpleResponse;
import kg.peaksoft.taskTrackerb6.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ParticipantService {

    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;
    private final BoardRepository boardRepository;
    private final UserWorkSpaceRepository userWorkSpaceRepository;

    public ParticipantResponse mapToResponse(User user) {
        ParticipantResponse participantResponse = new ParticipantResponse();
        participantResponse.setFirstName(user.getFirstName());
        participantResponse.setLastName(user.getLastName());
        participantResponse.setEmail(user.getEmail());
        participantResponse.setRole(user.getRole());
        return participantResponse;


    }

    public SimpleResponse deleteParticipantFromWorkspace(Long id, Long workspaceId) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User with this id " + id + " not found"));
        Workspace workspace = workspaceRepository.findById(workspaceId).orElseThrow(() -> new NotFoundException(" workspace with this id" + workspaceId + "not found"));
        workspace.getMembers().remove(user);
        for (UserWorkSpace userWorkSpace : userWorkSpaceRepository.findAll()) {
            if (userWorkSpace.getUser().equals(user)) {
                userWorkSpace.setUser(null);
            }
        }
        return new SimpleResponse("deleted", "ok");
    }

    public SimpleResponse deleteParticipantFromBoard(Long id, Long boardId) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User with  id" + id + " not found"));
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new NotFoundException("Board with id" + boardId + " not found"));
        board.getMembers().remove(user);
        for (Board b : boardRepository.findAll()) {
            user.setBoards(null);
            b.addUser(null);
        }
        return new SimpleResponse("deleted", "ok");
    }

    public List<ParticipantResponse> getAllParticipantFromBoard(Long boardId) {
        // Board board = boardRepository.findById(boardId).orElseThrow(() -> new NotFoundException("Board with id " + boardId + " not found"));
        List<ParticipantResponse> participantResponse = new ArrayList<>();
        for (User user : userRepository.getAllUserFromBoardId(boardId)) {
            participantResponse.add(mapToResponse(user));
        }
        return participantResponse;
    }

    public List<ParticipantResponse> getAllParticipantFromWorkspace(Long workspaceId, Long boardId) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("Workspace with id " + workspaceId + " not found"));
        Board board = boardRepository.findById(boardId).
                orElseThrow(() -> new NotFoundException("Board with id " + boardId + " not found"));

        List<User> allUserFromWorkspace = userRepository.getAllUserFromWorkspace(workspaceId);
        List<User> allUserFromBoardId = userRepository.getAllUserFromBoardId(boardId);

        List<ParticipantResponse> participantResponses = new ArrayList<>();
        participantResponses.add((ParticipantResponse) allUserFromWorkspace);
        participantResponses.add((ParticipantResponse) allUserFromBoardId);
        return participantResponses;
    }
}







