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
@Slf4j
@Transactional
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final ColumnRepository columnRepository;
    private final BoardRepository boardRepository;
    private final CardConverter converter;
    private final NotificationRepository notificationRepository;
    private final BasketRepository basketRepository;
    private final WorkspaceRepository workspaceRepository;

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


    public List<CardResponse> moveCard(Long cardId, Long columnId) {
        User user = getAuthenticateUser();
        Card card = cardRepository.findById(cardId).orElseThrow(
                () -> {
                    log.error("Card with id: {} not found!", cardId);
                    throw new NotFoundException("Card with id: " + cardId + " not found!");
                }
        );

        Column column = columnRepository.findById(columnId).orElseThrow(
                () -> {
                    log.error("Column with id: {} not found!", columnId);
                    throw new NotFoundException("Column with id: " + columnId + " not found!");
                }
        );

        Board board = boardRepository.findById(card.getColumn().getBoard().getId()).get();

        List<CardResponse> cardResponses = new ArrayList<>();
        for (Card c : column.getCards()) {
            cardResponses.add(converter.convertToResponseForGetAll(c));
        }

        if (!card.getColumn().equals(column)) {
            card.setMovedUser(user);
            card.setColumn(column);
            column.addCard(card);
            card.setColumn(column);
            Notification notification = new Notification();
            notification.setCard(card);
            notification.setNotificationType(NotificationType.CHANGE_STATUS);
            notification.setFromUser(user);
            notification.setUser(card.getColumn().getBoard().getWorkspace().getLead());
            notification.setBoard(board);
            notification.setCreatedAt(LocalDateTime.now());
            notification.setMessage("Card status with id: " + cardId + " has changed by " + user.getFirstName() + " " + user.getLastName());
            notificationRepository.save(notification);
            cardResponses.add(converter.convertToResponseForGetAll(cardRepository.save(card)));
        }

        log.info("Card with id: {} successfully moved!", cardId);
        return cardResponses;
    }

    public SimpleResponse deleteCard(Long id) {
        User user = getAuthenticateUser();
        Card card = cardRepository.findById(id).orElseThrow(
                () -> {
                    log.error("Card with id: {} not found!", id);
                    throw new NotFoundException("Card with id: " + id + " not found!");
                }
        );

        if (!user.equals(card.getCreator())) {
            log.error("You can not delete this card!");
            throw new BadCredentialException("You can not delete this card!");
        }

        List<Basket> baskets = user.getBaskets();
        if (card.getIsArchive().equals(true)) {
            if (baskets != null) {
                for (Basket b : baskets) {
                    if (b.getCard() != null && b.getCard().equals(card)) {
                        basketRepository.deleteBasket(b.getId());
                    }
                }
            }
        }

        cardRepository.deleteCard(card.getId());
        log.info("Card with id: {} successfully deleted", id);
        return new SimpleResponse("Card with id: " + id + " successfully deleted", "DELETE");
    }


    public CardInnerPageResponse getCard(Long id) {
        Card card = cardRepository.findById(id).orElseThrow(
                () -> {
                    log.error("Card with id: {} not found!", id);
                    throw new NotFoundException("Card with id: " + id + " not found!");
                }
        );

        return converter.convertToCardInnerPageResponse(card);
    }


    public List<CardResponse> getAllCardsByColumnId(Long id) {
        Column column = columnRepository.findById(id).orElseThrow(
                () -> {
                    log.error("Column with id: {} not found!", id);
                    throw new NotFoundException("Column with id: " + id + " not found!");
                }
        );

        List<CardResponse> getAllCards = new ArrayList<>();
        for (Card card : column.getCards()) {
            if (card.getIsArchive().equals(false)) {
                getAllCards.add(converter.convertToResponseForGetAll(card));
            }
        }

        return getAllCards;
    }


    public CardInnerPageResponse updateTitle(UpdateRequest request) {
        User user = getAuthenticateUser();
        Card card = cardRepository.findById(request.getId()).orElseThrow(
                () -> {
                    log.error("Card with id: {} not found!", request.getId());
                    throw new NotFoundException("Card with id: " + request.getId() + " not found!");
                }
        );

        if (!card.getCreator().equals(user)) {
            throw new BadCredentialException("You can not update this card title!");
        }

        card.setTitle(request.getNewTitle());
        log.info("Card with id:{} successfully updated!", card.getId());
        return converter.convertToCardInnerPageResponse(card);
    }


    public CardInnerPageResponse createCard(CardRequest request) {
        Column column = columnRepository.findById(request.getColumnId()).orElseThrow(
                () -> new NotFoundException("Column with id: " + request.getColumnId() + " not found!")
        );

        User user = getAuthenticateUser();
        Card card = new Card(request.getTitle(), request.getDescription(), user);
        card.setColumn(column);
        column.addCard(card);
        card.setCreatedAt(LocalDate.now());
        user.addCard(card);
        Card save = cardRepository.save(card);
        return converter.convertToCardInnerPageResponse(save);
    }


    public CardInnerPageResponse sentToArchive(Long id) {
        User user = getAuthenticateUser();
        Card card = cardRepository.findById(id).orElseThrow(
                () -> {
                    log.error("Card with id: {} not found!", id);
                    throw new NotFoundException("Card with id: " + id + " not found!");
                }
        );

        Column column = columnRepository.findById(card.getColumn().getId()).orElseThrow(
                () -> new NotFoundException("Column with id: " + id + " not found!")
        );

        Workspace workspace = workspaceRepository.findById(column.getBoard().getWorkspace().getId()).orElseThrow(
                () -> new NotFoundException("Workspace with id: " + column.getBoard().getWorkspace().getId() + " not found!")
        );

        if (card.getCreator().equals(user) || column.getCreator().equals(user) || workspace.getLead().equals(user)) {

            card.setIsArchive(!card.getIsArchive());
            if (card.getIsArchive().equals(true)) {
                Basket basket = new Basket();
                basket.setCard(card);
                basket.setArchivedUser(user);
                card.setBasket(basket);
                basketRepository.save(basket);
            }

            if (card.getIsArchive().equals(false)) {
                List<Basket> baskets = basketRepository.findAll();
                    for (Basket b : baskets) {
                        if (b.getCard() != null && b.getCard().equals(card)) {
                            basketRepository.deleteBasket(b.getId());
                        }
                }
            }
        } else {
            System.out.println("In card service!");
            throw new BadCredentialException("You can not archive this card!");
        }

        log.info("Card with id: {} successfully archived", id);
        return converter.convertToCardInnerPageResponse(card);
    }
}