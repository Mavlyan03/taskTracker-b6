package kg.peaksoft.taskTrackerb6.db.service;

import kg.peaksoft.taskTrackerb6.db.model.*;
import kg.peaksoft.taskTrackerb6.db.repository.*;
import kg.peaksoft.taskTrackerb6.dto.request.ColumnRequest;
import kg.peaksoft.taskTrackerb6.dto.request.UpdateColumnTitle;
import kg.peaksoft.taskTrackerb6.dto.response.ColumnResponse;
import kg.peaksoft.taskTrackerb6.dto.response.SimpleResponse;
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

        List<Card> cards = column.getCards();

        for (Card card : cards) {
            List<Notification> cardNotification = notificationRepository.findAllByCardId(card.getId());
            if (cardNotification != null) {
                for (Notification n : cardNotification) {
                    notificationRepository.deleteNotification(n.getId());
                }
            }
        }

        columnRepository.deleteColumn(column.getId());
        log.error("Column with id: {} successfully deleted", id);
        return new SimpleResponse("Column with id: " + id + " successfully deleted", "DELETE");
    }

    public List<ColumnResponse> findAllColumns(Long id) {
        List<Column> columns = columnRepository.findAllColumns(id);
        List<ColumnResponse> columnResponses = new ArrayList<>();
        for (Column column : columns) {
            ColumnResponse response = new ColumnResponse(column);
            response.setCreator(userRepository.getCreatorResponse(column.getCreator().getId()));
            columnResponses.add(response);
        }

        log.info("Get all columns");
        return columnResponses;
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

        if (!column.getCreator().equals(user) || !workspace.getLead().equals(user)) {
            throw new BadCredentialException("You can not archive this column!");
        }

        column.setIsArchive(!column.getIsArchive());
        if (column.getIsArchive().equals(true)) {
            Basket basket = new Basket();
            basket.setArchivedUser(user);
            basket.setColumn(column);
            basket.setArchivedUser(user);
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
        }

        Column column1 = columnRepository.save(column);
        log.info("Column with id: {} successfully archived", id);
        ColumnResponse response = new ColumnResponse(column1);
        response.setCreator(userRepository.getCreatorResponse(column1.getCreator().getId()));
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

        if (!column.getCreator().equals(user) || !workspace.getLead().equals(user)) {
            throw new BadCredentialException("You can not archive!");
        }

        List<Card> cards = column.getCards();
        if (cards == null) {
            throw new BadRequestException("This column is empty!");
        }
            for (Card card : column.getCards()) {
                Basket cardBasket = new Basket();
                card.setIsArchive(true);
                cardBasket.setCard(card);
                cardBasket.setArchivedUser(user);
                basketRepository.save(cardBasket);
            }

        return new SimpleResponse("All cards in column with id: " + column.getId() + " is archived!", "ARCHIVE");
    }
}
