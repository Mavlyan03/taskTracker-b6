package kg.peaksoft.taskTrackerb6.db.service;

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
    private final LabelRepository labelRepository;
    private final EstimationRepository estimationRepository;
    private final SubTaskRepository subTaskRepository;
    private final WorkspaceRepository workspaceRepository;

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

        return convertToCardResponse(card);
    }


    public List<CardResponseForGetAllCard> getAllCardsByColumnId(Long id) {
        Column column = columnRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Column with id: " + id + " not found!")
        );

        List<CardResponseForGetAllCard> getAllCards = new ArrayList<>();
        for (Card card : column.getCards()) {
            if (card.getIsArchive().equals(false)) {
                getAllCards.add(new CardResponseForGetAllCard(card));
            }
        }

        return getAllCards;
    }


    public CardResponseForGetById updateTitle(UpdateCardTitleRequest request) {
        Card card = cardRepository.findById(request.getId()).orElseThrow(
                () -> new NotFoundException("Card with id: " + request.getId() + " not found!")
        );

        card.setTitle(request.getNewTitle());
        return convertToCardResponse(card);
    }


    public CardResponseForGetById createCard(CardRequest request) {
        User user = getAuthenticateUser();
        Card card = convertToEntity(request);
        card.setCreator(user);
        card.setCreatedAt(LocalDate.now());
        return convertToCardResponse(cardRepository.save(card));
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

        return convertToCardResponse(card);
    }


    public List<CardResponseForGetAllCard> getAllArchivedCardsByBoardId(Long id) {
        Board board = boardRepository.findById(id).get();
        List<CardResponseForGetAllCard> responses = new ArrayList<>();
        for (Column column : board.getColumns()) {
            for (Card c : column.getCards()) {
                if (c.getIsArchive().equals(true)) {
                    responses.add(new CardResponseForGetAllCard(c));
                }
            }
        }
        return responses;
    }


    private Card convertToEntity(CardRequest request) {
        User user = getAuthenticateUser();
        Column column = columnRepository.findById(request.getColumnId()).orElseThrow(
                () -> new NotFoundException("Column with id: " + request.getColumnId() + " not found!")
        );

        Board board = boardRepository.findById(column.getBoard().getId()).get();
        Workspace workspace = workspaceRepository.findById(board.getWorkspace().getId()).get();
        Card card = new Card(request.getTitle(), request.getDescription());
        card.setColumn(column);
        card.setBoard(board);
        column.addCard(card);


        for (LabelRequest l : request.getLabelRequests()) {
            Label label = new Label(l.getDescription(), l.getColor());
            label.setCard(card);
            card.addLabel(label);
        }

        Estimation estimation = new Estimation(request.getEstimationRequest().getStartDate(), request.getEstimationRequest().getDeadlineDate(), request.getEstimationRequest().getReminder());
        card.setEstimation(estimation);
        estimation.setUser(user);
        estimation.setCard(card);

        List<MemberResponse> workspaceMember = new ArrayList<>();
        for (UserWorkSpace u : workspace.getUserWorkSpaces()) {
            if (!user.equals(u.getUser())) {
                workspaceMember.add(convertToMemberResponse(u.getUser()));
            }

        }

        for (MemberResponse memberResponse : workspaceMember) {
            for (MemberRequest m : request.getMemberRequests()) {
                if (memberResponse.getEmail().equals(m.getEmail())) {
                    card.addMember(convertMemberToUser(m));
                }
            }
        }

        for (ChecklistRequest c : request.getChecklistRequests()) {
            Checklist checklist = new Checklist(c.getTitle(), c.getTaskTracker());

            for (SubTaskRequest s : c.getSubTaskRequests()) {
                SubTask subTask = new SubTask(s.getDescription(), s.getIsDone());
                checklist.addSubTaskToChecklist(subTask);
                subTask.setChecklist(checklist);
            }
            card.addChecklist(checklist);
            checklist.setCard(card);
        }

        for (CommentRequest commentRequest : request.getCommentRequests()) {
            Comment comment = new Comment(commentRequest.getText(), LocalDateTime.now());
            comment.setUser(user);
            comment.setCard(card);
            card.addComment(comment);
        }

        return card;
    }

    private CardResponseForGetById convertToCardResponse(Card card) {
        CardResponseForGetById response = new CardResponseForGetById();
        response.setId(card.getId());
        response.setTitle(card.getTitle());
        response.setDescription(card.getDescription());
        response.setLabelResponses(labelRepository.getAllLabelResponses(card.getId()));
        response.setEstimationResponse(estimationRepository.getEstimationByCardId(card.getId()));
        if (card.getMembers()!= null){
            response.setMemberResponses(getAllCardMembers(card.getMembers()));
        }
        response.setChecklistResponses(getChecklistResponses(card.getChecklists()));
        response.setCommentResponses(getCommentResponses(card.getComments()));
        return response;
    }

    private List<CommentResponse> getCommentResponses(List<Comment> comments) {
        List<CommentResponse> commentResponses = new ArrayList<>();
        for (Comment c : comments) {
            commentResponses.add(convertCommentToResponse(c));
        }

        return commentResponses;
    }

    private CommentResponse convertCommentToResponse(Comment comment) {
        User user = getAuthenticateUser();
        return new CommentResponse(comment.getId(), comment.getText(), comment.getCreatedDate(), convertToCommentedUserResponse(user));
    }

    private CommentedUserResponse convertToCommentedUserResponse(User user) {
        return new CommentedUserResponse(user.getId(), user.getFirstName(), user.getPhotoLink());
    }

    private ChecklistResponse convertChecklistToResponse(Checklist checklist) {
        return new ChecklistResponse(checklist.getId(), checklist.getTitle(), checklist.getTaskTracker(), subTaskRepository.getSubTaskResponseByChecklistId(checklist.getId()));
    }

    private List<ChecklistResponse> getChecklistResponses(List<Checklist> checklists) {
        List<ChecklistResponse> responses = new ArrayList<>();
        for (Checklist c : checklists) {
            responses.add(convertChecklistToResponse(c));
        }

        return responses;
    }

    private List<MemberResponse> getAllCardMembers(List<User> users) {
        List<MemberResponse> memberResponses = new ArrayList<>();
        for (User user : users) {
            memberResponses.add(convertToMemberResponse(user));
        }
        return memberResponses;
    }

    private MemberResponse convertToMemberResponse(User user) {
        return new MemberResponse(
                user.getId(),
                user.getFirstName(),
                user.getEmail(),
                user.getPhotoLink()
        );
    }

    private User convertMemberToUser(MemberRequest request) {
        return userRepository.findByEmail(request.getEmail()).get();
    }
}
