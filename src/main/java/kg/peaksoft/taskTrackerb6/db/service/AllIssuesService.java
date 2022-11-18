package kg.peaksoft.taskTrackerb6.db.service;

import kg.peaksoft.taskTrackerb6.db.model.*;
import kg.peaksoft.taskTrackerb6.db.repository.CardRepository;
import kg.peaksoft.taskTrackerb6.db.repository.LabelRepository;
import kg.peaksoft.taskTrackerb6.db.repository.UserRepository;
import kg.peaksoft.taskTrackerb6.db.repository.WorkspaceRepository;
import kg.peaksoft.taskTrackerb6.dto.response.SearchCard;
import kg.peaksoft.taskTrackerb6.dto.response.AllIssuesResponse;
import kg.peaksoft.taskTrackerb6.dto.response.CardMemberResponse;
import kg.peaksoft.taskTrackerb6.dto.response.LabelResponse;
import kg.peaksoft.taskTrackerb6.enums.LabelsColor;
import kg.peaksoft.taskTrackerb6.exceptions.BadCredentialException;
import kg.peaksoft.taskTrackerb6.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AllIssuesService {

    private final WorkspaceRepository workspaceRepository;
    private final LabelRepository labelRepository;
    private final UserRepository userRepository;
    private final CardRepository cardRepository;

    public List<AllIssuesResponse> allIssues(Long workspaceId) {
        Workspace workspace = workspaceRepository.findById(workspaceId).orElseThrow(
                () -> new NotFoundException("Workspace not found!")
        );

        List<AllIssuesResponse> allIssuesResponses = new ArrayList<>();
        for (Card card : workspace.getAllIssues()) {
            allIssuesResponses.add(convertToResponse(card));
        }

        return allIssuesResponses;
    }


    private AllIssuesResponse convertToResponse(Card card) {
        AllIssuesResponse response = new AllIssuesResponse(card);
        List<CardMemberResponse> cardMemberResponses = new ArrayList<>();
        int isDoneCounter = 0;
        int allSubTasksCounter = 0;

        int period = Period.between(card.getEstimation().getStartDate(), card.getEstimation().getDueDate()).getDays();
        response.setPeriod("" + period + " days");

        for (User user : card.getMembers()) {
            cardMemberResponses.add(new CardMemberResponse(user));
        }

        response.setAssignee(cardMemberResponses);

        List<LabelResponse> labelResponses = labelRepository.getAllLabelResponses(card.getId());
        response.setLabels(labelResponses);

        for (Checklist checklist : card.getChecklists()) {
            for (SubTask subTask : checklist.getSubTasks()) {
                allSubTasksCounter++;
                if (subTask.getIsDone().equals(true)) {
                    isDoneCounter++;
                }
            }

            String checklist1 = "" + isDoneCounter + "/" + allSubTasksCounter;
            response.setChecklist(checklist1);
        }

        return response;
    }

    public SearchCard filterByCreatedDate(Long id, LocalDate from, LocalDate to) {
        SearchCard response = new SearchCard();
        if (from != null && to != null) {

            if (from.isAfter(to)) {
                throw new BadCredentialException("Please make a valid request");
            }

            response.setResponses(allIssuesResponsesList(cardRepository.searchCardByCreatedAt(id, from, to)));
        }

        return response;
    }

    private List<AllIssuesResponse> allIssuesResponsesList(List<Card> cards) {
        List<AllIssuesResponse> responses = new ArrayList<>();
        for (Card card : cards) {
            responses.add(convertToResponse(card));
        }

        return responses;
    }

    public List<AllIssuesResponse> filterByLabelColor(Long id, List<LabelsColor> colors) {
        Workspace workspace = workspaceRepository.findById(id).get();
        List<Card> workspaceCards = workspace.getAllIssues();
        List<AllIssuesResponse> allIssues = new ArrayList<>();
        for (Card card : workspaceCards) {
            for (Label label : card.getLabels()) {
                for (LabelsColor color : colors) {
                    if (color.equals(label.getColor())) {
                        allIssues.add(convertToResponse(card));
                    }
                }
            }
        }

        return allIssues;
    }

    public List<AllIssuesResponse> getAllMemberAssignedCards(Long workspaceId, Long memberId) {
        Workspace workspace = workspaceRepository.findById(workspaceId).get();
        User user = userRepository.findById(memberId).get();
        List<Card> cards = workspace.getAllIssues();
        List<AllIssuesResponse> memberAssignedCards = new ArrayList<>();
        for (Card card : cards) {
            for (User member : card.getMembers()) {
                if (member.equals(user)) {
                    memberAssignedCards.add(convertToResponse(card));
                }
            }
        }

        return memberAssignedCards;
    }
}