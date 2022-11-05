package kg.peaksoft.taskTrackerb6.db.service;

import kg.peaksoft.taskTrackerb6.db.converter.CardConverter;
import kg.peaksoft.taskTrackerb6.db.model.*;
import kg.peaksoft.taskTrackerb6.db.repository.*;
import kg.peaksoft.taskTrackerb6.dto.request.*;
import kg.peaksoft.taskTrackerb6.dto.response.*;
import kg.peaksoft.taskTrackerb6.exceptions.BadCredentialException;
import kg.peaksoft.taskTrackerb6.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
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

    private User getAuthenticateUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();
        return userRepository.findByEmail(login).orElseThrow(() ->
                new NotFoundException("User not found!"));
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


    public CardResponseForGetById getCard(Long id) {
        Card card = cardRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Card with id: " + id + " not found!")
        );

        return converter.convertToCardResponseForGetById(card);
    }


    public List<CardResponseForGetAllCard> getAllCardsByColumnId(Long id) {
        Column column = columnRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Column with id: " + id + " not found!")
        );

        List<CardResponseForGetAllCard> getAllCards = new ArrayList<>();
        for (Card card : column.getCards()) {
            if (card.getIsArchive().equals(false)) {
//                getAllCards.add(new CardResponseForGetAllCard(card));
                getAllCards.add(converter.convertToResponseForGetAll(card));
            }
        }

        return getAllCards;
    }


    public CardResponseForGetById updateTitle(UpdateCardTitleRequest request) {
        Card card = cardRepository.findById(request.getId()).orElseThrow(
                () -> new NotFoundException("Card with id: " + request.getId() + " not found!")
        );

        card.setTitle(request.getNewTitle());
        return converter.convertToCardResponseForGetById(card);
    }


    public CardResponseForGetById createCard(CardRequest request) {
        User user = getAuthenticateUser();
        Card card = converter.convertToEntity(request);
        card.setCreator(user);
        card.setCreatedAt(LocalDate.now());
        return converter.convertToCardResponseForGetById(cardRepository.save(card));
    }


    public CardResponseForGetById sentToArchive(Long id) {
        Card card = cardRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Card with id: " + id + " not found!")
        );

        Basket basket = new Basket();
        card.setIsArchive(!card.getIsArchive());
        if (card.getIsArchive().equals(true)) {
            basket.setCard(card);
        }

        return converter.convertToCardResponseForGetById(card);
    }


    public List<CardResponseForGetAllCard> getAllArchivedCardsByBoardId(Long id) {
        Board board = boardRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Board with id: " + id + " not found!")
        );

        List<CardResponseForGetAllCard> responses = new ArrayList<>();
        for (Column column : board.getColumns()) {
            for (Card c : column.getCards()) {
                if (c.getIsArchive().equals(true)) {
//                    responses.add(new CardResponseForGetAllCard(c));
                    responses.add(converter.convertToResponseForGetAll(c));
                }
            }
        }
        return responses;
    }


}
