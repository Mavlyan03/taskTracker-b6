package kg.peaksoft.taskTrackerb6.db.service;

import kg.peaksoft.taskTrackerb6.db.model.Card;
import kg.peaksoft.taskTrackerb6.db.model.Checklist;
import kg.peaksoft.taskTrackerb6.db.model.SubTask;
import kg.peaksoft.taskTrackerb6.db.model.User;
import kg.peaksoft.taskTrackerb6.db.repository.CardRepository;
import kg.peaksoft.taskTrackerb6.db.repository.ChecklistRepository;
import kg.peaksoft.taskTrackerb6.db.repository.UserRepository;
import kg.peaksoft.taskTrackerb6.dto.request.ChecklistRequest;
import kg.peaksoft.taskTrackerb6.dto.request.MemberRequest;
import kg.peaksoft.taskTrackerb6.dto.request.UpdateRequest;
import kg.peaksoft.taskTrackerb6.dto.response.ChecklistResponse;
import kg.peaksoft.taskTrackerb6.dto.response.SimpleResponse;
import kg.peaksoft.taskTrackerb6.dto.response.SubTaskResponse;
import kg.peaksoft.taskTrackerb6.exceptions.NoSuchElementException;
import kg.peaksoft.taskTrackerb6.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;


@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ChecklistService {

    private final CardRepository cardRepository;
    private final ChecklistRepository checklistRepository;
    private final UserRepository userRepository;

    public ChecklistResponse createChecklist(Long id, ChecklistRequest request) {
        Card card = cardRepository.findById(id).orElseThrow(
                () -> {
                    log.error("Card with id: {} not found!", id);
                    throw new NoSuchElementException(Card.class, id);
                }
        );

        Checklist checklist = new Checklist();
        checklist.setTitle(request.getTitle());

        checklist.setCard(card);
        card.addChecklist(checklist);
        log.info("Checklist successfully created");
        return convertToResponse(checklistRepository.save(checklist));
    }

    public ChecklistResponse updateTitle(UpdateRequest request) {
        Checklist checklist = checklistRepository.findById(request.getId()).orElseThrow(
                () -> {
                    log.error("Checklist with id: {} not found!", request.getId());
                    throw new NotFoundException("Checklist with id: " + request.getId() + " not found!");
                }
        );

        checklist.setTitle(request.getNewTitle());
        log.info("Checklist title with id: {} successfully updated", request.getId());
        return convertToResponse(checklistRepository.save(checklist));
    }

    public SimpleResponse deleteChecklist(Long id) {
        Checklist checklist = checklistRepository.findById(id).orElseThrow(
                () -> {
                    log.error("Checklist with id: {} not found!", id);
                    throw new NotFoundException("Checklist with id: " + id + " not found!");
                }
        );

        for (SubTask subTask : checklist.getSubTasks()) {
            subTask.setChecklist(null);
        }

        checklistRepository.delete(checklist);
        log.info("Checklist with id: {} successfully deleted!", id);
        return new SimpleResponse("Checklist with id " + id + " successfully deleted", "DELETE");
    }

    public List<ChecklistResponse> findAllChecklistsByCardId(Long id) {
        List<Checklist> checklists = checklistRepository.findAllChecklists(id);
        List<ChecklistResponse> checklistResponses = new ArrayList<>();
        for (Checklist checklist : checklists) {
            checklistResponses.add(convertToResponse(checklist));
        }

        log.info("Get all checklists by card's id");
        return checklistResponses;
    }

    public ChecklistResponse convertToResponse(Checklist checklist) {
        List<SubTask> allSubTasks = new ArrayList<>();
        if (checklist.getSubTasks() != null) {
            allSubTasks = checklist.getSubTasks();
        }

        List<SubTaskResponse> subTaskResponses = new ArrayList<>();
        int countOfSubTasks = 0;
        int countOfCompletedSubTask = 0;
        if (allSubTasks != null) {
            for (SubTask subTask : allSubTasks) {
                countOfSubTasks++;
                subTaskResponses.add(new SubTaskResponse(
                                subTask.getId(),
                                subTask.getDescription(),
                                subTask.getIsDone()
                        )
                );
                if (subTask.getIsDone().equals(true)) {
                    countOfCompletedSubTask++;
                }
            }

            int count;
            if (countOfCompletedSubTask <= 0) {
                count = 0;
            } else {
                count = (countOfCompletedSubTask * 100) / countOfSubTasks;
            }
            checklist.setCount(count);
            checklistRepository.save(checklist);
        }

        return new ChecklistResponse(checklist.getId(), checklist.getTitle(),
                countOfCompletedSubTask, countOfSubTasks,
                checklist.getCount(), subTaskResponses);
    }

    public User getAuthenticateUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();
        return userRepository.findUserByEmail(login).orElseThrow(
                () -> {
                    log.error("User not found!");
                    throw new NoSuchElementException("User not found!");
                }
        );
    }

    public User convertMemberToUser(MemberRequest request) {
        return userRepository.findUserByEmail(request.getEmail()).orElseThrow(
                () -> {
                    log.error("Email not found!");
                    throw new NoSuchElementException("Email not found!");
                }
        );
    }
}
