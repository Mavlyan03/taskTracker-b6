package kg.peaksoft.taskTrackerb6.db.service;

import kg.peaksoft.taskTrackerb6.db.model.Board;
import kg.peaksoft.taskTrackerb6.db.model.Workspace;
import kg.peaksoft.taskTrackerb6.db.repository.BoardRepository;
import kg.peaksoft.taskTrackerb6.db.repository.WorkspaceRepository;
import kg.peaksoft.taskTrackerb6.dto.request.BoardRequest;
import kg.peaksoft.taskTrackerb6.dto.response.BoardResponse;
import kg.peaksoft.taskTrackerb6.dto.response.SimpleResponse;
import kg.peaksoft.taskTrackerb6.exceptions.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BoardService {

    private final BoardRepository boardRepository;

    private final WorkspaceRepository workspaceRepository;

    public BoardService(BoardRepository boardRepository,
                        WorkspaceRepository workspaceRepository) {
        this.boardRepository = boardRepository;
        this.workspaceRepository = workspaceRepository;
    }

    public SimpleResponse createBoard(BoardRequest boardRequest) {

        Board board = new Board(boardRequest);

        Workspace workspace = workspaceRepository.findById(boardRequest.getWorkspaceId()).orElseThrow(
                () -> new NotFoundException("Workspace with id: " + boardRequest.getWorkspaceId() + " not found!"));

        workspace.addBoard(board);

        boardRepository.save(board);

        return new SimpleResponse("Completed.", "New board is successfully saved to workspace.");
    }

    public SimpleResponse deleteBoard(Long id) {
        Optional<Board> boardById = boardRepository.findById(id);
        boardById.ifPresentOrElse(
                (board -> boardRepository.deleteById(id)),
                () -> {
                    throw new NotFoundException(String.format(
                            "Board with such id: %d , didn't found", id));
                }
        );

        return new SimpleResponse("DELETED",
                "Board with id " + id + " is successfully deleted.");
    }

    public SimpleResponse isFavorite(Long id) {
        Board board = boardRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format("Board with =%s id not " +
                        "found", id)));
        board.setIsFavorite(!board.getIsFavorite());
        String a;
        if (board.getIsFavorite()) {
            a = "Favorite";
        } else {
            a = "Not favorite" +
                    "";
        }
        return new SimpleResponse(String.format("Board with = %s id is = %s", id, a), "ok");
    }

    public SimpleResponse changeBackground(Long id, BoardRequest boardRequest) {
        Board board = boardRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format("Board with =%s id not " +
                        "found", id)));
        board.setBackground(boardRequest.getBackground());
        boardRepository.save(board);
        return new SimpleResponse("Background updated", "Updated");
    }

    public BoardResponse getBoardById(Long id) {

        Board board = boardRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Board with id: " + id + " not found!"));

        return new BoardResponse();
    }
}