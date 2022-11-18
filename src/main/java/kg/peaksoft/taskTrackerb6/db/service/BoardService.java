package kg.peaksoft.taskTrackerb6.db.service;

import kg.peaksoft.taskTrackerb6.db.model.Board;
import kg.peaksoft.taskTrackerb6.db.model.Workspace;
import kg.peaksoft.taskTrackerb6.db.repository.BoardRepository;
import kg.peaksoft.taskTrackerb6.db.repository.WorkspaceRepository;
import kg.peaksoft.taskTrackerb6.dto.request.BoardRequest;
import kg.peaksoft.taskTrackerb6.dto.response.ArchiveBoardResponse;
import kg.peaksoft.taskTrackerb6.dto.response.BoardResponse;
import kg.peaksoft.taskTrackerb6.dto.response.SimpleResponse;
import kg.peaksoft.taskTrackerb6.exceptions.BadCredentialException;
import kg.peaksoft.taskTrackerb6.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BoardService {

    private final BoardRepository boardRepository;
    private final WorkspaceRepository workspaceRepository;

    public BoardResponse createBoard(BoardRequest boardRequest) {
        Board board = new Board(boardRequest);
        Workspace workspace = workspaceRepository.findById(boardRequest.getWorkspaceId()).orElseThrow(
                () -> {
                    log.error("Workspace with id:{} not found",boardRequest.getWorkspaceId());
                    throw new NotFoundException("Workspace with id: " + boardRequest.getWorkspaceId() + " not found!");
                }
        );

        workspace.addBoard(board);
        board.setWorkspace(workspace);
        boardRepository.save(board);
        return new BoardResponse(board.getId(),
                board.getTitle(),
                board.getIsFavorite(),
                board.getBackground());
    }

    public SimpleResponse deleteBoardById(Long id, Board board) {
        Board board1 = boardRepository.findById(id).orElseThrow(
                () -> {
                    log.error("board with id: " + id + " not found!");

                    throw new NotFoundException("board with id: " + id + " not found!");
                }
        );

        if (board1.getIsArchive().equals(board.getIsArchive())) {
            throw new BadCredentialException("You can not delete this board!");
        } else {
            boardRepository.delete(board);
            return new SimpleResponse(
                    "Board with id " + id + " is deleted successfully!", "DELETE");
        }
    }

    public BoardResponse makeFavorite(Long id) {
        Board board = boardRepository.findById(id).orElseThrow(
                () -> {
                    log.error("Board with id %s not found", id);
                    throw new NotFoundException(String.format("Board with id %s not found", id));
                }
        );

        board.setIsFavorite(!board.getIsFavorite());
        Board board1 = boardRepository.save(board);
        return new BoardResponse(board1.getId(),
                board1.getTitle(),
                board1.getIsFavorite(),
                board1.getBackground());
    }

    public BoardResponse changeBackground(Long id, BoardRequest boardRequest) {
        Board board = boardRepository.findById(id).orElseThrow(
                () -> {
                    log.error("Board with %s id not found", id);

                 throw  new NotFoundException(String.format("Board with %s id not found", id));
                }
        );

        board.setBackground(boardRequest.getBackground());
        boardRepository.save(board);
        return new BoardResponse(
                board.getId(),
                board.getTitle(),
                board.getIsFavorite(),
                board.getBackground()
        );
    }

    public BoardResponse updateTitle(Long id, BoardRequest boardRequest) {
        Board board = boardRepository.findById(id).orElseThrow(
                () -> {
                    log.error("Board with %s id not found", id);
                    throw new NotFoundException(String.format("Board with %s id not found", id));
                }
        );

        board.setTitle(boardRequest.getTitle());
        boardRepository.save(board);

        return new BoardResponse(
                board.getId(),
                board.getTitle(),
                board.getIsFavorite(),
                board.getBackground());
    }

    public BoardResponse getBoardById(Long id) {
        Board board = boardRepository.findById(id).orElseThrow(
                () -> {
                    log.error("Board with id: " + id + " not found!");
                    throw new NotFoundException("Board with id: " + id + " not found!");
                }
        );

        return new BoardResponse(
                board.getId(),
                board.getTitle(),
                board.getIsFavorite(),
                board.getBackground()
        );
    }

    private ArchiveBoardResponse convertToArchiveBoardResponse(Board board) {
        return new ArchiveBoardResponse(
                board.getId(),
                board.getTitle(),
                board.getBackground(),
                board.getIsArchive()
        );
    }

    public List<ArchiveBoardResponse> getAllArchiveBoardsList() {
        List<ArchiveBoardResponse> archiveBoards = new ArrayList<>();
        List<Board> boards = boardRepository.findAllByIsArchive();
        for (Board board : boards) {
            archiveBoards.add(convertToArchiveBoardResponse(board));
        }

        return archiveBoards;
    }

    public BoardResponse sendToArchive(Long id) {
        Board board = boardRepository.findById(id).orElseThrow(
                () -> {
                    log.error("Board with id %s not found", id);

                    throw new NotFoundException(String.format("Board with id %s not found", id));
                }
        );

        board.setIsArchive(!board.getIsArchive());
        Board board1 = boardRepository.save(board);
        return new BoardResponse(board1.getId(),
                board1.getTitle(),
                board1.getIsFavorite(),
                board1.getBackground()
        );
    }

    public List<BoardResponse> getAllBoardsByWorkspaceId(Long id) {
            return boardRepository.findAllBoards(id);
        }
}