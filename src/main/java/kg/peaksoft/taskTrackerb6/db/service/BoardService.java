package kg.peaksoft.taskTrackerb6.db.service;

import kg.peaksoft.taskTrackerb6.db.converter.CardConverter;
import kg.peaksoft.taskTrackerb6.db.model.*;
import kg.peaksoft.taskTrackerb6.db.repository.*;
import kg.peaksoft.taskTrackerb6.dto.request.BoardRequest;
import kg.peaksoft.taskTrackerb6.dto.request.UpdateRequest;
import kg.peaksoft.taskTrackerb6.dto.response.*;
import kg.peaksoft.taskTrackerb6.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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
    private final ColumnRepository columnRepository;
    private final CardRepository cardRepository;
    private final BasketRepository basketRepository;
    private final AttachmentRepository attachmentRepository;
    private final ChecklistRepository checklistRepository;
    private final SubTaskRepository subTaskRepository;
    private final CommentRepository commentRepository;
    private final NotificationRepository notificationRepository;
    private final EstimationRepository estimationRepository;


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
        boardRepository.save(board);
        log.info("Board successfully created");
        return new BoardResponse(board.getId(),
                board.getTitle(),
                board.getIsFavorite(),
                board.getBackground(),
                workspace.getId()
        );
    }

    public SimpleResponse deleteBoardById(Long id) {
        Board board = boardRepository.findById(id).orElseThrow(
                () -> {
                    log.error("Board with id: {} not found!", id);
                    throw new NotFoundException("Board with id: " + id + " not found!");
                }
        );

        for (Column column : columnRepository.findAllColumnsByBoardId(board.getId())) {
            List<Basket> columnBasket = basketRepository.findAll();
            for (Basket basket : columnBasket) {
                if (basket.getColumn() != null && basket.getColumn().equals(column)){
                    basketRepository.deleteBasket(basket.getId());
                    log.info("column in basket deleted");
                }
            }

            for (Card card : cardRepository.findCardsByColumnId(column.getId())) {
                List<Basket> baskets = basketRepository.findAll();
                if (card.getIsArchive().equals(true)) {
                    for (Basket b : baskets) {
                        if (b.getCard() != null && b.getCard().equals(card)) {
                            basketRepository.deleteBasket(b.getId());
                            log.info("delete card in basket");
                        }
                    }
                }

                for (Checklist c : checklistRepository.findAllChecklists(card.getId())) {
                    for (SubTask s : c.getSubTasks()) {
                        subTaskRepository.deleteSubTask(s.getId());
                        log.info("subTask deleted");
                    }

                    checklistRepository.deleteChecklist(c.getId());
                    log.info("checklist deleted");
                }

                for (Comment comment : commentRepository.findAllCommentsByCardId(card.getId())) {
                    commentRepository.deleteComment(comment.getId());
                    log.info("comment deleted");
                }

                List<Notification> cardNotification = notificationRepository.findAllByCardId(card.getId());
                if (cardNotification != null) {
                    for (Notification n : cardNotification) {
                        notificationRepository.deleteNotification(n.getId());
                        log.info("card notification deleted");
                    }
                }

                Estimation estimation = estimationRepository.findEstimationByCardId(card.getId());
                if (estimation != null) {
                    Notification notification = notificationRepository.findNotification(estimation.getId());
                    if (notification != null) {
                        notificationRepository.deleteNotification(notification.getId());
                    }

                    estimationRepository.deleteEstimation(estimation.getId());
                    log.info("estimation deleted");
                }

                List<Attachment> attachments = attachmentRepository.getAllByCardId(card.getId());
                if (attachments != null) {
                    for (Attachment a : attachments) {
                        log.info("Before delete attachment");
                        attachmentRepository.deleteAttachment(a.getId());
                        log.info("After delete attachment");
                    }

                }

                log.info("Card attachments: " + attachments);

                card.setLabels(null);
                card.setMembers(null);
                cardRepository.deleteCard(card.getId());
                log.info("card deleted");
            }

            columnRepository.deleteColumn(column.getId());
            log.info("column deleted");
        }

        board.setMembers(null);
        boardRepository.delete(board);
        log.info("Board with id: {} successfully deleted!", id);
        return new SimpleResponse("Board with id " + id + " is deleted successfully!", "DELETE");
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

    public BoardResponse updateTitle(UpdateRequest request) {
        Board board = boardRepository.findById(request.getId()).orElseThrow(
                () -> {
                    log.error("Board with id: {} id not found", request.getId());
                    throw new NotFoundException(String.format("Board with %s id not found", request.getId()));
                }
        );

        Workspace workspace = workspaceRepository.findById(board.getWorkspace().getId()).orElseThrow(
                () -> new NotFoundException("Workspace with id: " + board.getWorkspace().getId() + " not found!")
        );

        board.setTitle(request.getNewTitle());
        boardRepository.save(board);
        log.info("Board title with id: {} successfully updated!", request.getId());
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

        List<Column> columns = columnRepository.findAllColumnsByBoardId(board.getId());
        List<ColumnResponse> archivedColumns = new ArrayList<>();
        List<CardResponse> archivedCardResponse = new ArrayList<>();
        for (Column column : columns) {
            for (Card card : cardRepository.findCardsByColumnId(column.getId())) {
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