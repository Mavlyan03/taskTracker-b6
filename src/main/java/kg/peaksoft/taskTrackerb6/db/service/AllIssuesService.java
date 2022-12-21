package kg.peaksoft.taskTrackerb6.db.service;

import kg.peaksoft.taskTrackerb6.db.model.*;
import kg.peaksoft.taskTrackerb6.db.repository.*;
import kg.peaksoft.taskTrackerb6.dto.response.SearchCard;
import kg.peaksoft.taskTrackerb6.dto.response.AllIssuesResponse;
import kg.peaksoft.taskTrackerb6.dto.response.CardMemberResponse;
import kg.peaksoft.taskTrackerb6.dto.response.LabelResponse;
import kg.peaksoft.taskTrackerb6.exceptions.BadCredentialException;
import kg.peaksoft.taskTrackerb6.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class AllIssuesService {

    private final WorkspaceRepository workspaceRepository;
    private final LabelRepository labelRepository;
    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final EstimationRepository estimationRepository;

    public List<AllIssuesResponse> allIssues(Long workspaceId) {
        Workspace workspace = workspaceRepository.findById(workspaceId).orElseThrow(
                () -> {
                    log.error("Workspace with id: {} not found!", workspaceId);
                    throw new NotFoundException("Workspace with id: " + workspaceId + " not found!");
                }
        );

        List<AllIssuesResponse> allIssuesResponses = new ArrayList<>();
        for (Card card : cardRepository.findAllByWorkspaceId(workspace.getId())) {
            allIssuesResponses.add(convertToResponse(card));
        }

        return allIssuesResponses;
    }


    private AllIssuesResponse convertToResponse(Card card) {
        AllIssuesResponse response = new AllIssuesResponse(card);
        List<CardMemberResponse> cardMemberResponses = new ArrayList<>();
        int isDoneCounter = 0;
        int allSubTasksCounter = 0;

        Estimation estimation = estimationRepository.findEstimationByCardId(card.getId());
        if (estimation != null) {
            int period = Period.between(estimation.getStartDate(), estimation.getDueDate()).getDays();
            response.setPeriod("" + period + " days");
        }

        for (User user : card.getMembers()) {
            cardMemberResponses.add(new CardMemberResponse(user));
        }

        response.setAssignee(cardMemberResponses);

        List<LabelResponse> labelResponses = getAllLabelsByCardId(card.getId());
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
                log.error("Not valid request!");
                throw new BadCredentialException("Not valid request!");
            }

            response.setResponses(allIssuesResponsesList(cardRepository.searchCardByCreatedAt(id, from, to)));
        }

        log.info("Filter cards by created date");
        return response;
    }

    private List<AllIssuesResponse> allIssuesResponsesList(List<Card> cards) {
        List<AllIssuesResponse> responses = new ArrayList<>();
        for (Card card : cards) {
            responses.add(convertToResponse(card));
        }

        log.info("Get all issues responses list!");
        return responses;
    }

    public List<AllIssuesResponse> filterByLabelColor(Long id, List<Label> labels) {
        Workspace workspace = workspaceRepository.findById(id).get();
        List<Card> workspaceCards = cardRepository.findAllByWorkspaceId(workspace.getId());
        List<AllIssuesResponse> allIssues = new ArrayList<>();
        for (Card card : workspaceCards) {
            for (Label label : card.getLabels()) {
                for (Label l : labels) {
                    if (l.equals(label)) {
                        allIssues.add(convertToResponse(card));
                    }
                }
            }
        }

        log.info("Filter cards by label's color!");
        return allIssues;
    }

    public List<AllIssuesResponse> getAllMemberAssignedCards(Long workspaceId, Long memberId) {
        Workspace workspace = workspaceRepository.findById(workspaceId).get();
        User user = userRepository.findById(memberId).get();
        List<Card> cards = cardRepository.findAllByWorkspaceId(workspace.getId());
        List<AllIssuesResponse> memberAssignedCards = new ArrayList<>();
        for (Card card : cards) {
            for (User member : card.getMembers()) {
                if (member.equals(user)) {
                    memberAssignedCards.add(convertToResponse(card));
                }
            }
        }

        log.info("Get all member assigned cards");
        return memberAssignedCards;
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

        log.info("Get all labels by card's id");
        return labelResponses;
    }
}