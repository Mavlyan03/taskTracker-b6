package kg.peaksoft.taskTrackerb6.db.service;

import kg.peaksoft.taskTrackerb6.db.converter.CardConverter;
import kg.peaksoft.taskTrackerb6.db.model.*;
import kg.peaksoft.taskTrackerb6.db.repository.BoardRepository;
import kg.peaksoft.taskTrackerb6.db.repository.FavoriteRepository;
import kg.peaksoft.taskTrackerb6.db.repository.UserRepository;
import kg.peaksoft.taskTrackerb6.db.repository.WorkspaceRepository;
import kg.peaksoft.taskTrackerb6.dto.request.BoardRequest;
import kg.peaksoft.taskTrackerb6.dto.response.*;
import kg.peaksoft.taskTrackerb6.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BoardService {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final FavoriteRepository favoriteRepository;
    private final WorkspaceRepository workspaceRepository;
    private final CardConverter converter;

    private User getAuthenticateUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();
        return userRepository.findUserByEmail(login).orElseThrow(() ->
                new NotFoundException("User not found!"));
    }

    public BoardResponse createBoard(BoardRequest boardRequest) {
        Board board = new Board(boardRequest);
        Workspace workspace = workspaceRepository.findById(boardRequest.getWorkspaceId()).orElseThrow(
                () -> {
                    log.error("Workspace with id:{} not found", boardRequest.getWorkspaceId());
                    throw new NotFoundException("Workspace with id: " + boardRequest.getWorkspaceId() + " not found!");
                }
        );

        workspace.addBoard(board);
        board.setWorkspace(workspace);
        board.setCreatedAt(LocalDateTime.now());
        boardRepository.save(board);
        log.info("Board successfully created");
        return new BoardResponse(board.getId(),
                board.getTitle(),
                board.getIsFavorite(),
                board.getBackground(),
                workspace.getId()
        );
    }

    public SimpleResponse deleteBoardById(Long id, Board board) {
        boardRepository.findById(id).orElseThrow(
                () -> {
                    log.error("Board with id: {} not found!", id);
                    throw new NotFoundException("Board with id: " + id + " not found!");
                }
        );
            boardRepository.delete(board);
            log.info("Board with id: {} successfully deleted!", id);
            return new SimpleResponse(
                    "Board with id " + id + " is deleted successfully!", "DELETE");
    }

    public BoardResponse makeFavorite(Long id) {
        User user = getAuthenticateUser();
        Board board = boardRepository.findById(id).orElseThrow(
                () -> {
                    log.error("Board with id : {} not found", id);
                    throw new NotFoundException(String.format("Board with id %s not found", id));
                }
        );

        Workspace workspace = workspaceRepository.findById(board.getWorkspace().getId()).orElseThrow(
                () -> new NotFoundException("Workspace with id: " + board.getWorkspace().getId() + " not found!")
        );

        List<Favorite> favorites = user.getFavorites();
        for (Favorite fav : favorites) {
            if (fav.getBoard() != null) {
                if (fav.getBoard().equals(board)) {
                    favoriteRepository.deleteFavorite(fav.getId());
                    log.info("Favorite is deleted!");
                    log.info("Board's favorite with id: {} successfully change to false", board.getId());
                    return new BoardResponse(
                            board.getId(),
                            board.getTitle(),
                            false,
                            board.getBackground(),
                            workspace.getId()
                    );
                }
            }
        }

        Favorite favorite = new Favorite(user, board);
        favoriteRepository.save(favorite);
        log.info("Favorite is saved!");
        user.addFavorite(favorite);
        log.info("Board's favorite with id: {} successfully change to true", board.getId());
        return new BoardResponse(
                board.getId(),
                board.getTitle(),
                true,
                board.getBackground(),
                workspace.getId()
        );
    }

    public BoardResponse changeBackground(Long id, BoardRequest boardRequest) {
        Board board = boardRepository.findById(id).orElseThrow(
                () -> {
                    log.error("Board with id: {} not found", id);
                    throw new NotFoundException(String.format("Board with %s id not found", id));
                }
        );

        Workspace workspace = workspaceRepository.findById(board.getWorkspace().getId()).orElseThrow(
                () -> new NotFoundException("Workspace with id: " + board.getWorkspace().getId() + " not foudn")
        );

        board.setBackground(boardRequest.getBackground());
        boardRepository.save(board);
        log.info("Board background with id: {} successfully changed!", id);
        return new BoardResponse(
                board.getId(),
                board.getTitle(),
                board.getIsFavorite(),
                board.getBackground(),
                workspace.getId()
        );
    }

    public BoardResponse updateTitle(Long id, BoardRequest boardRequest) {
        Board board = boardRepository.findById(id).orElseThrow(
                () -> {
                    log.error("Board with id: {} id not found", id);
                    throw new NotFoundException(String.format("Board with %s id not found", id));
                }
        );

        Workspace workspace = workspaceRepository.findById(board.getWorkspace().getId()).orElseThrow(
                () -> new NotFoundException("Workspace with id: " + board.getWorkspace().getId() + " not found!")
        );

        board.setTitle(boardRequest.getTitle());
        board.setCreatedAt(board.getCreatedAt());
        boardRepository.save(board);
        log.info("Board title with id: {} successfully updated!", id);
        return new BoardResponse(
                board.getId(),
                board.getTitle(),
                board.getIsFavorite(),
                board.getBackground(),
                workspace.getId());
    }

    public BoardResponse getBoardById(Long id) {
        Board board = boardRepository.findById(id).orElseThrow(
                () -> {
                    log.error("Board with id: " + id + " not found!");
                    throw new NotFoundException("Board with id: " + id + " not found!");
                }
        );

        Workspace workspace = workspaceRepository.findById(board.getWorkspace().getId()).orElseThrow(
                () -> new NotFoundException("Workspace with id: " + board.getWorkspace().getId() + " not found!")
        );


        return new BoardResponse(
                board.getId(),
                board.getTitle(),
                board.getIsFavorite(),
                board.getBackground(),
                workspace.getId()
        );
    }

//    private ArchiveBoardResponse convertToArchiveBoardResponse(Board board) {
//        return new ArchiveBoardResponse(
//                board.getId(),
//                board.getTitle(),
//                board.getBackground(),
//                board.getIsArchive()
//        );
//    }
//
//    public List<ArchiveBoardResponse> getAllArchiveBoardsList() {
//        List<ArchiveBoardResponse> archiveBoards = new ArrayList<>();
//        List<Board> boards = boardRepository.findAllByIsArchive();
//        for (Board board : boards) {
//            archiveBoards.add(convertToArchiveBoardResponse(board));
//        }
//
//        log.info("Get all archived boards");
//        return archiveBoards;
//    }
//
//    public BoardResponse sendToArchive(Long id) {
//        Board board = boardRepository.findById(id).orElseThrow(
//                () -> {
//                    log.error("Board with id: {} not found", id);
//                    throw new NotFoundException(String.format("Board with id %s not found", id));
//                }
//        );
//
//        Workspace workspace = workspaceRepository.findById(board.getWorkspace().getId()).orElseThrow(
//                () -> new NotFoundException("Workspace with id: " + board.getWorkspace().getId() + " not found!")
//        );
//
//        board.setIsArchive(!board.getIsArchive());
//        Board board1 = boardRepository.save(board);
//        return new BoardResponse(board1.getId(),
//                board1.getTitle(),
//                board1.getIsFavorite(),
//                board1.getBackground(),
//                workspace.getId()
//        );
//    }

    public List<BoardResponse> getAllBoardsByWorkspaceId(Long id) {
        User user = getAuthenticateUser();
        Workspace workspace = workspaceRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Workspace with id: " + id + " not found!")
        );

        List<Favorite> favorites = user.getFavorites();
        List<Board> workspaceBoards = boardRepository.getAll(workspace.getId());
        List<Board> userFavoriteBoards = new ArrayList<>();
        List<BoardResponse> boardResponses = new ArrayList<>();
        for (Favorite fav : favorites) {
            if (fav.getBoard() != null) {
                userFavoriteBoards.add(fav.getBoard());
            }
        }

        for (Board board : workspaceBoards) {
            if (userFavoriteBoards.contains(board)) {
                for (Board favBoard : userFavoriteBoards) {
                    if (favBoard.equals(board)) {
                        boardResponses.add(new BoardResponse(
                                        board.getId(),
                                        board.getTitle(),
                                        true,
                                        board.getBackground(),
                                        workspace.getId()
                                )
                        );
                    }
                }
            } else {
                boardResponses.add(new BoardResponse(
                                board.getId(),
                                board.getTitle(),
                                false,
                                board.getBackground(),
                                workspace.getId()
                        )
                );
            }
        }

        return boardResponses;
    }


    public ArchiveResponse getAllArchivedCardsByBoardId(Long id) {
        Board board = boardRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Board with id: " + id + " not found!")
        );

        List<Column> columns = board.getColumns();
        List<ColumnResponse> archivedColumns = new ArrayList<>();
        List<CardResponse> archivedCardResponse = new ArrayList<>();
        for (Column column : columns) {
            for (Card card : column.getCards()) {
                if (card.getIsArchive().equals(true)) {
                    archivedCardResponse.add(converter.convertToResponseForGetAll(card));

                }
            }

            if (column.getIsArchive().equals(true)) {
                ColumnResponse response = new ColumnResponse(column);
                response.setCreator(userRepository.getCreatorResponse(column.getCreator().getId()));
                archivedColumns.add(response);
            }
        }

        return new ArchiveResponse(archivedCardResponse, archivedColumns);
    }
}