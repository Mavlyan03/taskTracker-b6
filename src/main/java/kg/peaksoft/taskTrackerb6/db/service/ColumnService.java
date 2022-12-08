package kg.peaksoft.taskTrackerb6.db.service;

import kg.peaksoft.taskTrackerb6.db.converter.CardConverter;
import kg.peaksoft.taskTrackerb6.db.model.*;
import kg.peaksoft.taskTrackerb6.db.repository.*;
import kg.peaksoft.taskTrackerb6.dto.request.ColumnRequest;
import kg.peaksoft.taskTrackerb6.dto.request.UpdateColumnTitle;
import kg.peaksoft.taskTrackerb6.dto.response.*;
import kg.peaksoft.taskTrackerb6.exceptions.BadCredentialException;
import kg.peaksoft.taskTrackerb6.exceptions.BadRequestException;
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
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ColumnService {

    private final BoardRepository boardRepository;
    private final ColumnRepository columnRepository;
    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;
    private final BasketRepository basketRepository;
    private final NotificationRepository notificationRepository;
    private final CardRepository cardRepository;
    private final SubTaskRepository subTaskRepository;
    private final AttachmentRepository attachmentRepository;
    private final LabelRepository labelRepository;
    private final CommentRepository commentRepository;
    private final ChecklistRepository checklistRepository;
    private final EstimationRepository estimationRepository;
    private final CardConverter converter;

    private User getAuthenticateUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();
        return userRepository.findUserByEmail(login).orElseThrow(
                () -> {
                    log.error("User not found!");
                    throw new NotFoundException("User not found!");
                }
        );
    }

    public ColumnResponse createColumn(ColumnRequest columnRequest) {
        User user = getAuthenticateUser();
        Column column = new Column();
        column.setTitle(columnRequest.getColumnName());
        Board board = boardRepository.findById(columnRequest.getBoardId()).orElseThrow(
                () -> {
                    log.error("Board with id: {} not found", column.getBoard().getId());
                    throw new NotFoundException("Board with id: " + column.getBoard().getId() + " not found");
                }
        );

        board.addColumn(column);
        column.setBoard(board);
        column.setCreator(user);
        user.addColumn(column);
        Column column1 = columnRepository.save(column);
        log.info("Column successfully created");
        ColumnResponse response = new ColumnResponse(column1);
        response.setCreator(userRepository.getCreatorResponse(user.getId()));
        return response;
    }

    public ColumnResponse updateColumn(UpdateColumnTitle columnTitle) {
        User user = getAuthenticateUser();
        Column column = columnRepository.findById(columnTitle.getId()).orElseThrow(
                () -> {
                    log.error("Column with id: {} not found", columnTitle.getId());
                    throw new NotFoundException("Column with id: " + columnTitle.getId() + " not found");
                }
        );

        if (!column.getCreator().equals(user)) {
            throw new BadCredentialException("You can not update title this of this column!");
        }

        column.setTitle(columnTitle.getNewTitle());
        Column column1 = columnRepository.save(column);
        log.info("Column title with id: {} successfully updated", columnTitle.getId());
        ColumnResponse response = new ColumnResponse(column1);
        response.setCreator(userRepository.getCreatorResponse(user.getId()));
        List<CardResponse> cardResponses = new ArrayList<>();
        for (Card card : column1.getCards()) {
            if (card.getIsArchive().equals(false)) {
                cardResponses.add(converter.convertToResponseForGetAll(card));
            }
        }

        response.setCreator(userRepository.getCreatorResponse(column1.getCreator().getId()));
        response.setColumnCards(cardResponses);
        return response;
    }

    public SimpleResponse deleteColumn(Long id) {
        User user = getAuthenticateUser();
        Column column = columnRepository.findById(id).orElseThrow(
                () -> {
                    log.error("Column with id: " + id + " not found!");
                    throw new NotFoundException("Column with id: " + id + " not found!");
                }
        );

        if (!column.getCreator().equals(user)) {
            throw new BadCredentialException("You can not delete this column!");
        }

        List<Basket> baskets = user.getBaskets();
        if (baskets != null) {
            for (Basket b : baskets) {
                for (Card c : column.getCards()) {
                    if (b.getCard() != null && b.getCard().equals(c)) {
                        c.setIsArchive(false);
                        basketRepository.deleteBasket(b.getId());
                    }
                }

                if (b.getColumn() != null && b.getColumn().equals(column)) {
                    basketRepository.deleteBasket(b.getId());
                }
            }
        }

        List<Card> cards = column.getCards();
        for (Card card : cards) {
            List<Notification> cardNotification = notificationRepository.findAllByCardId(card.getId());
            if (cardNotification != null) {
                for (Notification n : cardNotification) {
                    notificationRepository.deleteNotification(n.getId());
                }
            }
            cardRepository.deleteCard(card.getId());
        }

        columnRepository.deleteColumn(column.getId());
        log.error("Column with id: {} successfully deleted", id);
        return new SimpleResponse("Column with id: " + id + " successfully deleted", "DELETE");
    }

    public AllBoardColumnsResponse findAllColumns(Long id) {
        Board board = boardRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Board with id: " + id + " not found!")
        );

        List<Column> columns = board.getColumns();
        AllBoardColumnsResponse boardColumnsResponse = new AllBoardColumnsResponse();
        List<ColumnResponse> columnResponses = new ArrayList<>();
        List<CardResponse> cardResponsesList = new ArrayList<>();
        for (Column column : columns) {
            ColumnResponse response = new ColumnResponse(column);
            response.setCreator(userRepository.getCreatorResponse(column.getCreator().getId()));
            for (Card card : column.getCards()) {
                if (card != null && card.getIsArchive().equals(false)) {
                    cardResponsesList.add(converter.convertToResponseForGetAll(card));
                    response.setColumnCards(cardResponsesList);
                }
            }

            columnResponses.add(response);
            boardColumnsResponse.setColumnResponses(columnResponses);
        }

        log.info("Get all columns");
        return boardColumnsResponse;
    }


    public ColumnResponse sentToArchive(Long id) {
        User user = getAuthenticateUser();
        Column column = columnRepository.findById(id).orElseThrow(
                () -> {
                    log.error("Column with id: {} not found!", id);
                    throw new NotFoundException("Column with id: " + id + " not found!");
                }
        );

        Board board = boardRepository.findById(column.getBoard().getId()).orElseThrow(
                () -> new NotFoundException("Board with id: " + column.getBoard().getId() + " not found!")
        );

        Workspace workspace = workspaceRepository.findById(board.getWorkspace().getId()).orElseThrow(
                () -> new NotFoundException("Workspace with id: " + board.getWorkspace().getId() + " not found!")
        );

        if (column.getCreator().equals(user) || workspace.getLead().equals(user)) {
            column.setIsArchive(!column.getIsArchive());
            if (column.getIsArchive().equals(true)) {
                Basket basket = new Basket();
                basket.setArchivedUser(user);
                basket.setColumn(column);
                basket.setArchivedUser(user);
                column.setBasket(basket);
                for (Card card : column.getCards()) {
                    Basket cardBasket = new Basket();
                    card.setIsArchive(true);
                    cardBasket.setCard(card);
                    cardBasket.setArchivedUser(user);
                    basketRepository.save(cardBasket);
                }

                basketRepository.save(basket);
            }

            if (column.getIsArchive().equals(false)) {
                List<Basket> baskets = basketRepository.findAll();
                for (Basket b : baskets) {
                    for (Card c : column.getCards()) {
                        if (b.getCard() != null && b.getCard().equals(c)) {
                            c.setIsArchive(false);
                            basketRepository.deleteBasket(b.getId());

                        }

                        if (b.getColumn() != null && b.getColumn().equals(column)) {
                            System.out.println("Before delete column");
                            basketRepository.deleteBasket(b.getId());
                            System.out.println("After delete column");
                        }
                    }
                }
            }
        } else {
            throw new BadCredentialException("You can not archive this column!");
        }

        log.info("Column with id: {} successfully archived", id);
        ColumnResponse response = new ColumnResponse(column);
        response.setCreator(userRepository.getCreatorResponse(column.getCreator().getId()));
        return response;
    }


    public SimpleResponse archiveAllCardsInColumn(Long columnId) {
        User user = getAuthenticateUser();
        Column column = columnRepository.findById(columnId).orElseThrow(
                () -> new NotFoundException("Column with id: " + columnId + " not found!")
        );

        Workspace workspace = workspaceRepository.findById(column.getBoard().getWorkspace().getId()).orElseThrow(
                () -> new NotFoundException("Workspace with id: " + column.getBoard().getWorkspace().getId() + " not found!")
        );

        if (column.getCreator().equals(user) || workspace.getLead().equals(user)) {
            List<Card> cards = column.getCards();
            if (cards == null) {
                throw new BadRequestException("This column is empty!");
            }
            for (Card card : column.getCards()) {
                Basket cardBasket = new Basket();
                if (card.getIsArchive().equals(false)) {
                    card.setIsArchive(true);
                    cardBasket.setCard(card);
                    cardBasket.setArchivedUser(user);
                    card.setBasket(cardBasket);
                    basketRepository.save(cardBasket);
                }
            }
        } else {
            throw new BadCredentialException("You can not archive!");
        }

        return new SimpleResponse("All cards in column with id: " + column.getId() + " is archived!", "ARCHIVE");
    }

    public SimpleResponse deleteAllCardsOfColumn(Long id) {
        User user = getAuthenticateUser();
        Column column = columnRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Column with id: " + id + " not found!")
        );

        Workspace workspace = workspaceRepository.findById(column.getBoard().getWorkspace().getId()).orElseThrow(
                () -> new NotFoundException("Workspace with id: " + column.getBoard().getWorkspace().getId() + " not found!")
        );

        if (!column.getCreator().equals(user) || !workspace.getLead().equals(user)) {
            throw new BadCredentialException("You can not delete!");
        }

        List<Card> cards = column.getCards();
        List<Basket> baskets = user.getBaskets();
        for (Card card : cards) {
            if (card.getIsArchive().equals(true)) {
                if (baskets != null) {
                    for (Basket b : baskets) {
                        if (b.getCard().equals(card)) {
                            basketRepository.deleteBasket(b.getId());
                        }
                    }
                }
            }

            for (Attachment attachment : card.getAttachments()) {
                attachmentRepository.deleteAttachment(attachment.getId());
            }

            for (Checklist c : checklistRepository.findAllChecklists(card.getId())) {
                for (SubTask s : c.getSubTasks()) {
                    Estimation estimation = s.getEstimation();
                    if (estimation != null) {
                        estimationRepository.deleteEstimation(estimation.getId());
                    }

                    subTaskRepository.deleteSubTask(s.getId());
                }

                checklistRepository.deleteChecklist(c.getId());
            }

            for (Comment comment : card.getComments()) {
                commentRepository.deleteComment(comment.getId());
            }

            for (Label label : card.getLabels()) {
                labelRepository.deleteLabel(label.getId());
            }

            List<Notification> cardNotification = notificationRepository.findAllByCardId(card.getId());
            if (cardNotification != null) {
                for (Notification n : cardNotification) {
                    notificationRepository.deleteNotification(n.getId());
                }
            }
            cardRepository.deleteCard(card.getId());
        }

        return new SimpleResponse("All card from column with id: " + column.getId() + " is deleted!", "DELETE");
    }
}
