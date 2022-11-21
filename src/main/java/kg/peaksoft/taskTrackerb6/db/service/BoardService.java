package kg.peaksoft.taskTrackerb6.db.service;

import kg.peaksoft.taskTrackerb6.db.model.Board;
import kg.peaksoft.taskTrackerb6.db.model.Favorite;
import kg.peaksoft.taskTrackerb6.db.model.User;
import kg.peaksoft.taskTrackerb6.db.model.Workspace;
import kg.peaksoft.taskTrackerb6.db.repository.BoardRepository;
import kg.peaksoft.taskTrackerb6.db.repository.FavoriteRepository;
import kg.peaksoft.taskTrackerb6.db.repository.UserRepository;
import kg.peaksoft.taskTrackerb6.db.repository.WorkspaceRepository;
import kg.peaksoft.taskTrackerb6.dto.request.BoardRequest;
import kg.peaksoft.taskTrackerb6.dto.response.ArchiveBoardResponse;
import kg.peaksoft.taskTrackerb6.dto.response.BoardResponse;
import kg.peaksoft.taskTrackerb6.dto.response.SimpleResponse;
import kg.peaksoft.taskTrackerb6.exceptions.BadCredentialException;
import kg.peaksoft.taskTrackerb6.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final FavoriteRepository favoriteRepository;
    private final WorkspaceRepository workspaceRepository;

    private User getAuthenticateUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();
        return userRepository.findUserByEmail(login).orElseThrow(() ->
                new NotFoundException("User not found!"));
    }

    public BoardResponse createBoard(BoardRequest boardRequest) {
        Board board = new Board(boardRequest);
        Workspace workspace = workspaceRepository.findById(boardRequest.getWorkspaceId()).orElseThrow(
                () -> new NotFoundException("Workspace with id: " + boardRequest.getWorkspaceId() + " not found!")
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
                () -> new NotFoundException("board with id: " + id + " not found!")
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
        User user = getAuthenticateUser();
        Board board = boardRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Board with id: " + id + " not found!")
        );

        board.setIsFavorite(!board.getIsFavorite());
        Boolean isTrue = !board.getIsFavorite();
        Board board1 = boardRepository.save(board);
        if (isTrue.equals(true)) {
            Favorite favorite = new Favorite(user, board1);
            user.addFavorite(favorite);
        } else {
            for (Favorite fav : user.getFavorites()) {
                if (fav.getBoard().equals(board1)) {
                    favoriteRepository.delete(fav);
                }
            }
        }

                return new BoardResponse(board1.getId(),
                board1.getTitle(),
                board1.getIsFavorite(),
                board1.getBackground());
    }


//    public BoardResponse makeFavorite(Long id) {
//        User user = getAuthenticateUser();
//        Board board = boardRepository.findById(id).orElseThrow(
//                () -> new NotFoundException(String.format("Board with id %s not found", id))
//        );
//
//        List<Favorite> favorites = user.getFavorites();
//        for (Favorite fav : favorites) {
//            if (fav.getBoard().equals(board)) {
//
//            }
//        }
//
//        board.setIsFavorite(true);
//        Board board1 = boardRepository.save(board);
//        Favorite favorite = new Favorite(user, board1);
//        favoriteRepository.save(favorite);
//        user.addFavorite(favorite);
//        return new BoardResponse(board1.getId(),
//                board1.getTitle(),
//                board1.getIsFavorite(),
//                board1.getBackground());
//    }
//
//    public BoardResponse makeNotFavorite(Long id) {
//        User user = getAuthenticateUser();
//        Board board = boardRepository.findById(id).orElseThrow(
//                () -> new NotFoundException("Board with id: " + id + " not found!")
//        );
//
//        List<Favorite> favorites = user.getFavorites();
//        for (Favorite favorite : favorites) {
//            if (favorite.getBoard().getClass().equals(board.getClass())) {
//                board.setIsFavorite(false);
//                favoriteRepository.deleteFavorite(favorite.getId());
//            }
//        }
//
//        Board board1 = boardRepository.save(board);
//        return new BoardResponse(board1.getId(),
//                board1.getTitle(),
//                board1.getIsFavorite(),
//                board1.getBackground());
//    }


    public BoardResponse changeBackground(Long id, BoardRequest boardRequest) {
        Board board = boardRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format("Board with %s id not found", id))
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
                () -> new NotFoundException(String.format("Board with %s id not found", id))
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
                () -> new NotFoundException("Board with id: " + id + " not found!")
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
                () -> new NotFoundException(String.format("Board with id %s not found", id))
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