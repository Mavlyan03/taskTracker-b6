package kg.peaksoft.taskTrackerb6.db.service;

import kg.peaksoft.taskTrackerb6.db.model.Card;
import kg.peaksoft.taskTrackerb6.db.model.Line;
import kg.peaksoft.taskTrackerb6.db.model.User;
import kg.peaksoft.taskTrackerb6.db.repository.CardRepository;
import kg.peaksoft.taskTrackerb6.db.repository.LineRepository;
import kg.peaksoft.taskTrackerb6.db.repository.UserRepository;
import kg.peaksoft.taskTrackerb6.dto.request.CardRequest1;
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
    private final LineRepository lineRepository;

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

    public CardResponse createCard(CardRequest1 request1) {
        User user = getAuthenticateUser();
        Line line = lineRepository.findById(request1.getColumnId()).get();
        Card card = new Card();
        card.setTitle(request1.getTitle());
        card.setDescription(request1.getDescription());
        card.setCreator(user);
        card.setCreatedAt(LocalDate.now());
        card.setIsArchive(card.getIsArchive());
        card.setLine(line);
        line.setCards(List.of(card));
        cardRepository.save(card);
        return new CardResponse(card.getId(), card.getTitle());
    }

    public CardResponse updateCardTitle(CardRequest1 cardRequest1) {
        Card card = cardRepository.findById(cardRequest1.getColumnId()).get();
        card.setTitle(cardRequest1.getTitle());
        cardRepository.save(card);
        return new CardResponse(card.getId(), card.getTitle());
    }

    public List<CardResponse> getAllCardByLineId(Long id) {
        Line line = lineRepository.findById(id).get();
        List<CardResponse> cardResponses = new ArrayList<>();
        for (Card card : line.getCards()) {
            cardResponses.add(new CardResponse(card));
        }
        return cardResponses;
    }

    public CardResponse sendToArchive(Long id) {
        Card card = cardRepository.findById(id).get();
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
