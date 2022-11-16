package kg.peaksoft.taskTrackerb6.db.service;

import kg.peaksoft.taskTrackerb6.db.converter.CardConverter;
import kg.peaksoft.taskTrackerb6.db.model.*;
import kg.peaksoft.taskTrackerb6.db.repository.*;
import kg.peaksoft.taskTrackerb6.dto.request.*;
import kg.peaksoft.taskTrackerb6.dto.response.*;
import kg.peaksoft.taskTrackerb6.enums.NotificationType;
import kg.peaksoft.taskTrackerb6.exceptions.BadCredentialException;
import kg.peaksoft.taskTrackerb6.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
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
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final ColumnRepository columnRepository;
    private final BoardRepository boardRepository;
    private final CardConverter converter;
    private final NotificationRepository notificationRepository;
    private final WorkspaceRepository workspaceRepository;

    private User getAuthenticateUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();
        return userRepository.findByEmail(login).orElseThrow(() ->
                new NotFoundException("User not found!"));
    }

    public List<CardResponse> moveCard(Long cardId, Long columnId) {
        User user = getAuthenticateUser();
        Card card = cardRepository.findById(cardId).orElseThrow(
                () -> new NotFoundException("Card with id: " + cardId + " not found!")
        );

        Column changedColumn = columnRepository.findById(columnId).orElseThrow(
                () -> new NotFoundException("Column with id: " + columnId + " not found!")
        );

        List<CardResponse> cardResponses = new ArrayList<>();
        for (Card c : changedColumn.getCards()) {
            cardResponses.add(converter.convertToResponseForGetAll(c));
        }

        if (!card.getColumn().equals(changedColumn)) {
            card.setMovedUser(user);
            changedColumn.addCard(card);
            card.setColumn(changedColumn);
            Notification notification = new Notification();
            notification.setCard(card);
            notification.setIsRead(false);
            notification.setNotificationType(NotificationType.CHANGE_STATUS);
            notification.setFromUser(user);
            notification.setUser(card.getCreator());
            notification.setCreatedAt(LocalDateTime.now());
            notification.setMessage("Card status with id: " + cardId + " has changed by " + user.getFirstName() + " " + user.getLastName());
            notificationRepository.save(notification);
            User recipient = card.getCreator();
            User workspaceCreator = card.getBoard().getWorkspace().getLead();
            workspaceCreator.setNotifications(List.of(notification));
            recipient.addNotification(notification);
            cardResponses.add(converter.convertToResponseForGetAll(card));
        }

        return cardResponses;
    }

    public SimpleResponse deleteCard(Long id) {
        User user = getAuthenticateUser();
        Card card = cardRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Card with id: " + id + " not found!")
        );

        if (!user.equals(card.getCreator())) {
            throw new BadCredentialException("You can not delete this card!");
        }

        cardRepository.delete(card);
        return new SimpleResponse("Card with id: " + id + " successfully deleted", "DELETE");
    }


    public CardInnerPageResponse getCard(Long id) {
        Card card = cardRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Card with id: " + id + " not found!")
        );

        return converter.convertToCardInnerPageResponse(card);
    }


    public List<CardResponse> getAllCardsByColumnId(Long id) {
        Column column = columnRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Column with id: " + id + " not found!")
        );

        List<CardResponse> getAllCards = new ArrayList<>();
        for (Card card : column.getCards()) {
            if (card.getIsArchive().equals(false)) {
                getAllCards.add(converter.convertToResponseForGetAll(card));
            }
        }

        return getAllCards;
    }


    public CardInnerPageResponse updateTitle(UpdateCardTitleRequest request) {
        Card card = cardRepository.findById(request.getId()).orElseThrow(
                () -> new NotFoundException("Card with id: " + request.getId() + " not found!")
        );

        card.setTitle(request.getNewTitle());
        return converter.convertToCardInnerPageResponse(card);
    }


    public CardInnerPageResponse createCard(CardRequest request) {
        User user = getAuthenticateUser();
        Card card = converter.convertToEntity(request);
        Board board = boardRepository.findById(card.getBoard().getId()).get();
        Workspace workspace = workspaceRepository.findById(board.getWorkspace().getId()).get();
        card.setCreator(user);
        card.setColumn(card.getColumn());
        card.setCreatedAt(LocalDate.now());
        workspace.addCard(card);
        return converter.convertToCardInnerPageResponse(cardRepository.save(card));
    }


    public CardInnerPageResponse sentToArchive(Long id) {
        Card card = cardRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Card with id: " + id + " not found!")
        );

        Basket basket = new Basket();
        card.setIsArchive(!card.getIsArchive());
        if (card.getIsArchive().equals(true)) {
            basket.setCard(card);
        }

        return converter.convertToCardInnerPageResponse(card);
    }


    public List<CardResponse> getAllArchivedCardsByBoardId(Long id) {
        Board board = boardRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Board with id: " + id + " not found!")
        );

        List<CardResponse> responses = new ArrayList<>();
        for (Column column : board.getColumns()) {
            for (Card c : column.getCards()) {
                if (c.getIsArchive().equals(true)) {
                    responses.add(converter.convertToResponseForGetAll(c));
                }
            }
        }
        return responses;
    }
}