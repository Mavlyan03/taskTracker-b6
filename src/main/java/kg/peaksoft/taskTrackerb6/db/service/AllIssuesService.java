package kg.peaksoft.taskTrackerb6.db.service;

import kg.peaksoft.taskTrackerb6.db.model.*;
import kg.peaksoft.taskTrackerb6.db.repository.BoardRepository;
import kg.peaksoft.taskTrackerb6.db.repository.WorkspaceRepository;
import kg.peaksoft.taskTrackerb6.dto.response.AllIssuesResponse;
import kg.peaksoft.taskTrackerb6.dto.response.CardMemberResponse;
import kg.peaksoft.taskTrackerb6.dto.response.LabelResponse;
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
    private final BoardRepository boardRepository;

    public List<AllIssuesResponse> allIssues(Long workspaceId) {
        Workspace workspace = workspaceRepository.findById(workspaceId).orElseThrow(
                () -> new NotFoundException("Workspace with id: " + workspaceId + " not found!")
        );

        List<AllIssuesResponse> allIssuesResponses = new ArrayList<>();
        for (Card card : workspace.getAllIssues()) {
            allIssuesResponses.add(convertToResponse(card));
        }

        return allIssuesResponses;
    }



    private AllIssuesResponse convertToResponse(Card card) {
        Board board = boardRepository.findById(card.getBoard().getId()).get();
        Workspace workspace = workspaceRepository.findById(board.getWorkspace().getId()).get();
        AllIssuesResponse response = new AllIssuesResponse(card);
        List<CardMemberResponse> cardMemberResponses = new ArrayList<>();
        List<LabelResponse> labelResponses = new ArrayList<>();
        int period = 0;
        int isDoneCounter = 0;
        int allSubTasksCounter = 0;
        for (Card c : workspace.getAllIssues()) {

            period = Period.between(c.getEstimation().getStartDate(), c.getEstimation().getDueDate()).getDays();


            for (User user : c.getMembers()) {
                cardMemberResponses.add(new CardMemberResponse(user));
            }

            for (Label label : c.getLabels()) {
                labelResponses.add(new LabelResponse(label));
            }

            for (Checklist checklist : c.getChecklists()) {
                for (SubTask s : checklist.getSubTasks()) {
                    allSubTasksCounter++;
                    if (s.getIsDone().equals(true)) {
                        isDoneCounter++;
                    }
                }
            }
        }

        String checklist = "" + isDoneCounter + "/" + allSubTasksCounter;
        response.setPeriod("" + period + " days");
        response.setChecklist(checklist);
        response.setMembers(cardMemberResponses);
        response.setLabels(labelResponses);

        return response;
    }
}
