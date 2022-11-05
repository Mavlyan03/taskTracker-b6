package kg.peaksoft.taskTrackerb6.db.converter;

import kg.peaksoft.taskTrackerb6.db.model.*;
import kg.peaksoft.taskTrackerb6.db.repository.*;
import kg.peaksoft.taskTrackerb6.dto.request.*;
import kg.peaksoft.taskTrackerb6.dto.response.*;
import kg.peaksoft.taskTrackerb6.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CardConverter {

    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final ColumnRepository columnRepository;
    private final LabelRepository labelRepository;
    private final EstimationRepository estimationRepository;
    private final WorkspaceRepository workspaceRepository;
    private final SubTaskRepository subTaskRepository;
    private final CardRepository cardRepository;

    private User getAuthenticateUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();
        return userRepository.findByEmail(login).orElseThrow(() ->
                new NotFoundException("User not found!"));
    }


    public Card convertToEntity(CardRequest request) {
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

        Estimation estimation = new Estimation(request.getEstimationRequest().getStartDate(), request.getEstimationRequest().getStartTime(), request.getEstimationRequest().getDueDate(), request.getEstimationRequest().getDeadlineTime(), request.getEstimationRequest().getReminder());
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

    public CardResponseForGetById convertToCardResponseForGetById(Card card) {
        CardResponseForGetById response = new CardResponseForGetById();
        response.setId(card.getId());
        response.setTitle(card.getTitle());
        response.setDescription(card.getDescription());
        response.setLabelResponses(labelRepository.getAllLabelResponses(card.getId()));
        response.setEstimationResponse(estimationRepository.getEstimationByCardId(card.getId()));
        if (card.getMembers() != null){
            response.setMemberResponses(getAllCardMembers(card.getMembers()));
        }
        response.setChecklistResponses(getChecklistResponses(card.getChecklists()));
        response.setCommentResponses(getCommentResponses(card.getComments()));
        return response;
    }

    public CardResponseForGetAllCard convertToResponseForGetAll(Card card) {
        CardResponseForGetAllCard response = new CardResponseForGetAllCard();
        response.setId(card.getId());
        response.setTitle(card.getTitle());
        response.setLabelResponses(labelRepository.getAllLabelResponses(card.getId()));
//        int between = Period.between(card.getEstimation().getStartDate(), card.getEstimation().getDeadlineDate())
//        response.setDuration();
        response.setNumberOfMembers(card.getMembers().size() + 1);
        int subTask = 0;
        for (Checklist checklist : card.getChecklists()) {
          subTask = checklist.getSubTasks().size();
        }

        response.setNumberOfSubTasks(subTask + 1);
        int completedSubTasks = 0;
        for (Checklist c : card.getChecklists()) {
            for (SubTask task : c.getSubTasks()) {
                if (task.getIsDone().equals(true)) {
                    completedSubTasks ++;
                }
            }
        }

        response.setNumberOfCompletedSubTask(completedSubTasks + 1);
        return response;
    }

    public List<CommentResponse> getCommentResponses(List<Comment> comments) {
        List<CommentResponse> commentResponses = new ArrayList<>();
        for (Comment c : comments) {
            commentResponses.add(convertCommentToResponse(c));
        }

        return commentResponses;
    }

    public CommentResponse convertCommentToResponse(Comment comment) {
        User user = getAuthenticateUser();
        return new CommentResponse(comment.getId(), comment.getText(), comment.getCreatedDate(), convertToCommentedUserResponse(user));
    }

    public CommentedUserResponse convertToCommentedUserResponse(User user) {
        return new CommentedUserResponse(user.getId(), user.getFirstName(), user.getPhotoLink());
    }

    public ChecklistResponse convertChecklistToResponse(Checklist checklist) {
        return new ChecklistResponse(checklist.getId(), checklist.getTitle(), checklist.getTaskTracker(), subTaskRepository.getSubTaskResponseByChecklistId(checklist.getId()));
    }

    public List<ChecklistResponse> getChecklistResponses(List<Checklist> checklists) {
        List<ChecklistResponse> responses = new ArrayList<>();
        for (Checklist c : checklists) {
            responses.add(convertChecklistToResponse(c));
        }

        return responses;
    }

    public List<MemberResponse> getAllCardMembers(List<User> users) {
        List<MemberResponse> memberResponses = new ArrayList<>();
        for (User user : users) {
            memberResponses.add(convertToMemberResponse(user));
        }
        return memberResponses;
    }

    public MemberResponse convertToMemberResponse(User user) {
        return new MemberResponse(
                user.getId(),
                user.getFirstName(),
                user.getEmail(),
                user.getPhotoLink()
        );
    }

    public User convertMemberToUser(MemberRequest request) {
        return userRepository.findByEmail(request.getEmail()).get();
    }

}
