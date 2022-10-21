package kg.peaksoft.taskTrackerb6.db.service;


import kg.peaksoft.taskTrackerb6.db.model.Board;
import kg.peaksoft.taskTrackerb6.db.model.User;
import kg.peaksoft.taskTrackerb6.db.model.Workspace;
import kg.peaksoft.taskTrackerb6.db.repository.BoardRepository;
import kg.peaksoft.taskTrackerb6.db.repository.UserRepository;
import kg.peaksoft.taskTrackerb6.db.repository.WorkspaceRepository;
import kg.peaksoft.taskTrackerb6.dto.response.SimpleResponse;
import kg.peaksoft.taskTrackerb6.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ParticipantService {

    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;
    private final BoardRepository boardRepository;

    public SimpleResponse deleteParticipantById(Long id, Long workspaceId) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User with this id " + id + " not found"));
        Workspace workspace = workspaceRepository.findById(workspaceId).orElseThrow(() -> new NotFoundException(" workspace with this id" + workspaceId + "not found"));
        if (workspace.getMembers().contains(user)) {
            userRepository.deleteById(id);
        }
        return new SimpleResponse("deleted", "ok");

    }

    public SimpleResponse deleteParticipantFromBoard(Long id, Long boardId) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User with  id" + id + " not found"));
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new NotFoundException("Board with id" + boardId + " not found"));
        if (board.getMembers().contains(user)) {
            userRepository.deleteById(id);
        }
        return new SimpleResponse("deleted", "ok");
    }

    public User getAllParticipantFromBoard(Long boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new NotFoundException("Board with id " + boardId + " not found"));
        return (User) board.getMembers();
    }

    public User getAllParticipantFromWorkspace(Long workspaceId) {
        Workspace workspace = workspaceRepository.findById(workspaceId).orElseThrow(() -> new NotFoundException("Workspace with id " + workspaceId + " not found"));
        return (User) workspace.getMembers();
    }
}



