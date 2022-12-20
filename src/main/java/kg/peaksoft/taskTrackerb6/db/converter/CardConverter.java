package kg.peaksoft.taskTrackerb6.db.converter;

import kg.peaksoft.taskTrackerb6.db.model.*;
import kg.peaksoft.taskTrackerb6.db.repository.*;
import kg.peaksoft.taskTrackerb6.db.service.ChecklistService;
import kg.peaksoft.taskTrackerb6.dto.response.*;
import kg.peaksoft.taskTrackerb6.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CardConverter {

    private final UserRepository userRepository;
    private final LabelRepository labelRepository;
    private final EstimationRepository estimationRepository;
    private final CardRepository cardRepository;
    private final ChecklistRepository checklistRepository;
    private final ChecklistService checklistService;
    private final CommentRepository commentRepository;


    private User getAuthenticateUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();
        return userRepository.findUserByEmail(login).orElseThrow(() ->
                new NotFoundException("User not found!"));
    }


    public CardInnerPageResponse convertToCardInnerPageResponse(Card card) {
        CardInnerPageResponse response = new CardInnerPageResponse(card);
        if (card.getLabels() != null) {
            response.setLabelResponses(getAllLabelsByCardId(card.getId()));
        } else {
            response.setLabelResponses(new ArrayList<>());
        }

        Estimation estimation = estimationRepository.findEstimationByCardId(card.getId());
        if (estimation != null) {
            response.setEstimationResponse(getEstimationByCardId(card.getId()));
        } else {
            response.setEstimationResponse(new EstimationResponse());
        }

        if (card.getMembers() != null) {
            response.setMemberResponses(getAllCardMembers(card.getMembers()));
        } else {
            response.setMemberResponses(new ArrayList<>());
        }

        response.setChecklistResponses(getChecklistResponses(checklistRepository.findAllChecklists(card.getId())));
        if (commentRepository.findAllCommentsByCardId(card.getId()) != null) {
            response.setCommentResponses(getCommentResponses(commentRepository.findAllCommentsByCardId(card.getId())));
        } else {
            response.setCommentResponses(new ArrayList<>());
        }

        response.setCreator(userRepository.getCreatorResponse(card.getCreator().getId()));
        return response;
    }

    public CardResponse convertToResponseForGetAll(Card card) {
        CardResponse response = new CardResponse(card);
        response.setCreator(userRepository.getCreatorResponse(card.getCreator().getId()));
        response.setLabelResponses(getAllLabelsByCardId(card.getId()));
        Estimation estimation = estimationRepository.findEstimationByCardId(card.getId());
        if (estimation != null) {
            int between = Period.between(estimation.getStartDate(), estimation.getDueDate()).getDays();
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
        if (comments == null) {
            return commentResponses;
        } else {
            for (Comment c : comments) {
                commentResponses.add(convertCommentToResponse(c));
            }
            return commentResponses;
        }
    }

    private CommentResponse convertCommentToResponse(Comment comment) {
        User user = getAuthenticateUser();
        return new CommentResponse(comment.getId(), comment.getText(), comment.getCreatedAt(), userRepository.getCommentedUserResponse(user.getId()));
    }

    private List<ChecklistResponse> getChecklistResponses(List<Checklist> checklists) {
        List<ChecklistResponse> responses = new ArrayList<>();
        if (checklists == null) {
            return responses;
        } else {
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
                user.getImage(),
                user.getRole()
        );
    }

    private EstimationResponse getEstimationByCardId(Long cardId) {
        Card card = cardRepository.findById(cardId).orElseThrow(
                () -> new NotFoundException("Card with id: " + cardId + " not found!")
        );

        Estimation estimation = estimationRepository.findEstimationByCardId(card.getId());

        return new EstimationResponse(
                estimation.getId(),
                estimation.getStartTime(),
                estimation.getDueTime(),
                estimation.getReminder()
        );
    }


    private List<LabelResponse> getAllLabelsByCardId(Long cardId) {
        Card card = cardRepository.findById(cardId).orElseThrow(
                () -> new NotFoundException("Card with id: " + cardId + " not found!")
        );

        List<Label> cardLabels = card.getLabels();
        List<LabelResponse> labelResponses = new ArrayList<>();
        for (Label l : cardLabels) {
            labelResponses.add(labelRepository.getLabelResponse(l.getId()));
        }

        return labelResponses;
    }
}
