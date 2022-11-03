package kg.peaksoft.taskTrackerb6.db.service;

import kg.peaksoft.taskTrackerb6.db.model.Board;
import kg.peaksoft.taskTrackerb6.db.model.Card;
import kg.peaksoft.taskTrackerb6.db.model.Column;
import kg.peaksoft.taskTrackerb6.db.model.User;
import kg.peaksoft.taskTrackerb6.db.repository.BoardRepository;
import kg.peaksoft.taskTrackerb6.db.repository.CardRepository;
import kg.peaksoft.taskTrackerb6.db.repository.ColumnRepository;
import kg.peaksoft.taskTrackerb6.db.repository.UserRepository;
import kg.peaksoft.taskTrackerb6.dto.request.CardRequest;
import kg.peaksoft.taskTrackerb6.dto.request.UpdateCardRequest;
import kg.peaksoft.taskTrackerb6.dto.response.CardResponse;
import kg.peaksoft.taskTrackerb6.dto.response.SimpleResponse;
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

    private User getAuthenticateUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();
        return userRepository.findByEmail(login).orElseThrow(() ->
                new NotFoundException("User not found!"));
    }

    public SimpleResponse deleteCard(Long id) {
        User user = getAuthenticateUser();
        Card card = cardRepository.findById(id).get();
        if (!card.getCreator().equals(user)) {
            throw new BadCredentialException("You can not delete this card!");
        }

        cardRepository.delete(card);
        return new SimpleResponse(
                "Card with id: " + id + " successfully deleted",
                "DELETE"
        );
    }

    public CardResponse createCard(CardRequest request) {
        User user = getAuthenticateUser();
        Column column = columnRepository.findById(request.getColumnId()).orElseThrow(
                () -> new NotFoundException("Column with id: " + request.getColumnId() + " not found!")
        );

        Card card = new Card();
        card.setTitle(request.getTitle());
        card.setDescription(request.getDescription());
        card.setCreator(user);
        card.setCreatedAt(LocalDate.now());
        card.setIsArchive(card.getIsArchive());
        card.setColumn(column);
        Board board = boardRepository.findById(column.getBoard().getId()).get();
        card.setBoard(board);
        column.setCards(List.of(card));
        cardRepository.save(card);
        return new CardResponse(card.getId(), card.getTitle());
    }

    public CardResponse updateCardTitle(UpdateCardRequest request) {
        Card card = cardRepository.findById(request.getId()).orElseThrow(
                () -> new NotFoundException("Card with id: " + request.getId() + " not found!")
        );

        card.setTitle(request.getNewTitle());
        cardRepository.save(card);
        return new CardResponse(card.getId(), card.getTitle());
    }

    public CardResponse sendToArchive(Long id) {
        Card card = cardRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Card with id: " + id + " not found!")
        );

        card.setIsArchive(!card.getIsArchive());
        Card archivedCard = cardRepository.save(card);
        return new CardResponse(archivedCard);
    }

    public CardResponse getCardById(Long id) {
        Card card = cardRepository.findById(id).get();
        return new CardResponse(card);
    }

    public List<CardResponse> getAllCardsByColumnIdWithQuery(Long id) {
        return cardRepository.findAllCardResponse(id);
    }

    public List<CardResponse> getAllArchivedCards(Long boardId) {
        return cardRepository.findAllArchivedCards(boardId);
    }
}
