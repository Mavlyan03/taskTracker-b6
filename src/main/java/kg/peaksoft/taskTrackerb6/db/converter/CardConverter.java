package kg.peaksoft.taskTrackerb6.db.converter;

import kg.peaksoft.taskTrackerb6.db.model.*;
import kg.peaksoft.taskTrackerb6.db.repository.*;
import kg.peaksoft.taskTrackerb6.db.service.ChecklistService;
import kg.peaksoft.taskTrackerb6.dto.request.*;
import kg.peaksoft.taskTrackerb6.dto.response.*;
import kg.peaksoft.taskTrackerb6.enums.NotificationType;
import kg.peaksoft.taskTrackerb6.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.Period;
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
    private final CardRepository cardRepository;
    private final ChecklistRepository checklistRepository;
    private final ChecklistService checklistService;
    private final NotificationRepository notificationRepository;

    private User getAuthenticateUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();
        return userRepository.findUserByEmail(login).orElseThrow(() ->
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

        Estimation estimation = new Estimation(
                request.getEstimationRequest().getStartDate(),
                request.getEstimationRequest().getDueDate(),
                request.getEstimationRequest().getReminder());

        estimation.setUser(user);
        estimation.setStartTime(convertTimeToEntity(request.getEstimationRequest().getStartTime()));
        estimation.setDeadlineTime(convertTimeToEntity(request.getEstimationRequest().getDeadlineTime()));
        card.setEstimation(estimation);

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
                    Notification notification = new Notification();
                    notification.setCard(card);
                    notification.setIsRead(false);
                    notification.setNotificationType(NotificationType.ASSIGN);
                    notification.setFromUser(user);
                    notification.setUser(convertMemberToUser(m));
                    notification.setCreatedAt(LocalDateTime.now());
                    notification.setMessage("You has assigned to " + card.getId() + ", by " + user.getFirstName() + " " + user.getLastName());
                    notificationRepository.save(notification);
                    User recipient = convertMemberToUser(m);
                    recipient.addNotification(notification);
                }
            }
        }

        for (ChecklistRequest c : request.getChecklistRequests()) {
            Checklist checklist = new Checklist(c.getTitle());

            List<MemberResponse> members = new ArrayList<>();
            for (UserWorkSpace u : workspace.getUserWorkSpaces()) {
                if (!user.equals(u.getUser())){
                    members.add(convertToMemberResponse(u.getUser()));
                }
            }

            for (SubTaskRequest s : c.getSubTaskRequests()) {
                SubTask subTask = new SubTask(s.getDescription(), s.getIsDone());
                for (MemberResponse memberResponse : members) {
                    for (MemberRequest memberRequest : s.getMemberRequests()) {
                        if (memberResponse.getEmail().equals(memberRequest.getEmail())){
                            subTask.addMember(convertMemberToUser(memberRequest));
                        }
                    }
                }
                checklist.addSubTaskToChecklist(subTask);
                subTask.setChecklist(checklist);
                if (s.getEstimationRequest() != null){
                    Estimation estimation1 = new Estimation();
                        estimation1.setStartDate(s.getEstimationRequest().getStartDate());
                        estimation1.setDueDate(s.getEstimationRequest().getDueDate());
                        estimation1.setReminder(s.getEstimationRequest().getReminder());
                        estimation1.setUser(user);
                        estimation1.setStartTime(convertTimeToEntity(s.getEstimationRequest().getStartTime()));
                        estimation1.setDeadlineTime(convertTimeToEntity(s.getEstimationRequest().getDeadlineTime()));
                        subTask.setEstimation(estimation1);
                        estimation1.setSubTask(subTask);
                }
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

    public CardInnerPageResponse convertToCardInnerPageResponse(Card card) {
        CardInnerPageResponse response = new CardInnerPageResponse();
        response.setId(card.getId());
        response.setTitle(card.getTitle());
        response.setDescription(card.getDescription());
        if (card.getLabels() != null) {
            response.setLabelResponses(labelRepository.getAllLabelResponses(card.getId()));
        }

        if (card.getEstimation() != null) {
            response.setEstimationResponse(getEstimationByCardId(card.getId()));
        }

        if (card.getMembers() != null) {
            response.setMemberResponses(getAllCardMembers(card.getMembers()));
        }

        response.setChecklistResponses(getChecklistResponses(checklistRepository.findAllChecklists(card.getId())));
        if (card.getComments() != null) {
            response.setCommentResponses(getCommentResponses(card.getComments()));
        }
        return response;
    }

    public CardResponse convertToResponseForGetAll(Card card) {
        CardResponse response = new CardResponse();
        response.setId(card.getId());
        response.setTitle(card.getTitle());
        response.setLabelResponses(labelRepository.getAllLabelResponses(card.getId()));
        if (card.getEstimation() != null) {
            int between = Period.between(card.getEstimation().getStartDate(), card.getEstimation().getDueDate()).getDays();
            response.setDuration("" + between + " days");
        }

        response.setNumberOfMembers(card.getMembers().size());
        int subTask = 0;
        for (Checklist checklist : checklistRepository.findAllChecklists(card.getId())) {
            for (int i = 0; i < checklist.getSubTasks().size(); i++) {
            subTask++;
            }
        }

        response.setNumberOfSubTasks(subTask);
        int completedSubTasks = 0;
        for (Checklist c : card.getChecklists()) {
            for (SubTask task : c.getSubTasks()) {
                if (task.getIsDone().equals(true)) {
                    completedSubTasks++;
                }
            }
        }

        response.setNumberOfCompletedSubTask(completedSubTasks);
        return response;
    }

    private List<CommentResponse> getCommentResponses(List<Comment> comments) {
        List<CommentResponse> commentResponses = new ArrayList<>();
        if (comments == null){
            return commentResponses;
        }else {
            for (Comment c : comments) {
                commentResponses.add(convertCommentToResponse(c));
            }
            return commentResponses;
        }
    }

    private CommentResponse convertCommentToResponse(Comment comment) {
        User user = getAuthenticateUser();
        return new CommentResponse(comment.getId(), comment.getText(), comment.getCreatedDate(), convertToCommentedUserResponse(user));
    }

    private CommentedUserResponse convertToCommentedUserResponse(User user) {
        return new CommentedUserResponse(user.getId(), user.getFirstName(), user.getLastName(), user.getPhotoLink());
    }

    private List<ChecklistResponse> getChecklistResponses(List<Checklist> checklists) {
        List<ChecklistResponse> responses = new ArrayList<>();
        if (checklists == null){
            return responses;
        }else {
            for (Checklist c : checklists) {
                responses.add(checklistService.convertToResponse(c));
            }
            return responses;
        }
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
                user.getLastName(),
                user.getEmail(),
                user.getPhotoLink()
        );
    }

    private User convertMemberToUser(MemberRequest request) {
        return userRepository.findUserByEmail(request.getEmail()).get();
    }

    private EstimationResponse getEstimationByCardId(Long cardId) {
        Card card = cardRepository.findById(cardId).orElseThrow(
                () -> new NotFoundException("Card with id: " + cardId + " not found!")
        );

        Estimation estimation = estimationRepository.findById(card.getEstimation().getId()).orElseThrow(
                () -> new NotFoundException("Estimation not found!")
        );

        return new EstimationResponse(estimation.getId(), estimation.getStartDate(), convertStartTimeToResponse(estimation.getStartTime()), estimation.getDueDate(), convertStartTimeToResponse(estimation.getDeadlineTime()), estimation.getReminder());
    }

    private MyTimeClass convertTimeToEntity(MyTimeClassRequest startClass) {
        MyTimeClass myTimeClass = new MyTimeClass();
        myTimeClass.setTime(startClass.getHour(), startClass.getMinute());
        return myTimeClass;
    }

    private MyTimeClassResponse convertStartTimeToResponse(MyTimeClass timeClass) {
        return new MyTimeClassResponse(timeClass.getId(), String.format("%02d:%02d", timeClass.getHour(), timeClass.getMinute()));
    }

}
