package kg.peaksoft.taskTrackerb6.db.service;

import kg.peaksoft.taskTrackerb6.db.model.Checklist;
import kg.peaksoft.taskTrackerb6.db.model.Estimation;
import kg.peaksoft.taskTrackerb6.db.model.SubTask;
import kg.peaksoft.taskTrackerb6.db.model.User;
import kg.peaksoft.taskTrackerb6.db.model.UserWorkSpace;
import kg.peaksoft.taskTrackerb6.db.model.Workspace;
import kg.peaksoft.taskTrackerb6.db.repository.ChecklistRepository;
import kg.peaksoft.taskTrackerb6.db.repository.SubTaskRepository;
import kg.peaksoft.taskTrackerb6.db.repository.UserRepository;
import kg.peaksoft.taskTrackerb6.db.repository.WorkspaceRepository;
import kg.peaksoft.taskTrackerb6.dto.request.MemberRequest;
import kg.peaksoft.taskTrackerb6.dto.request.SubTaskRequest;
import kg.peaksoft.taskTrackerb6.dto.response.EstimationResponse;
import kg.peaksoft.taskTrackerb6.dto.response.MemberResponse;
import kg.peaksoft.taskTrackerb6.dto.response.SimpleResponse;
import kg.peaksoft.taskTrackerb6.dto.response.SubTaskResponse;
import kg.peaksoft.taskTrackerb6.exceptions.NoSuchElementException;
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
public class SubTaskService {

    private final ChecklistRepository checklistRepository;
    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;
    private final ChecklistService checklistService;
    private final SubTaskRepository subTaskRepository;

    public SubTaskResponse createSubTask(Long id, SubTaskRequest request) {

        User currentUser = getCurrentUser();
        Checklist checklist = checklistRepository.findById(id).orElseThrow(() -> {
                    log.error("Checklist with id: {} not found!", id);
                    throw new NoSuchElementException(Checklist.class, id);
                }
        );

        Workspace workspace = workspaceRepository.findById(checklist.getCard().getBoard().getWorkspace().getId()).orElseThrow(
                () -> {
                    log.error("Workspace with id: {} not found!", checklist.getCard().getBoard().getWorkspace().getId());
                    throw new NoSuchElementException(Workspace.class, checklist.getCard().getBoard().getWorkspace().getId());
                }
        );

        SubTask subTask = new SubTask();
        subTask.setIsDone(request.getIsDone());
        subTask.setDescription(request.getDescription());
        List<MemberResponse> memberResponses = new ArrayList<>();
        for (UserWorkSpace userWorkSpace : workspace.getUserWorkSpaces()) {
            memberResponses.add(convertToMemberResponse(userWorkSpace.getUser()));
        }

        for (MemberResponse memberResponse : memberResponses) {
            for (MemberRequest memberRequest : request.getMemberRequests()) {
                if (memberResponse.getEmail().equals(memberRequest.getEmail())) {
                    subTask.addMember(checklistService.convertMemberToUser(memberRequest));
                }
            }
        }

        if (request.getEstimationRequest() != null) {
            Estimation estimation = new Estimation();
            estimation.setStartDate(request.getEstimationRequest().getStartDate());
            estimation.setDueDate(request.getEstimationRequest().getDueDate());
            estimation.setReminder(request.getEstimationRequest().getReminder());
            estimation.setStartTime(checklistService.convertTimeToEntity(request.getEstimationRequest().getDeadlineTime()));
            estimation.setDeadlineTime(checklistService.convertTimeToEntity(request.getEstimationRequest().getDeadlineTime()));
            estimation.setUser(currentUser);
            subTask.setEstimation(estimation);
            estimation.setSubTask(subTask);
        }

        subTask.setChecklist(checklist);
        checklist.addSubTaskToChecklist(subTask);
        log.info("SubTask successfully created");
        return convertToResponse(subTaskRepository.save(subTask));
    }

    public SubTaskResponse updateDescription(Long id, SubTaskRequest request) {
        SubTask subTask = subTaskRepository.findById(id).orElseThrow(() -> {
                    log.error("SubTask with id: {} not found!", id);
                    throw new NoSuchElementException(SubTask.class, id);
                }
        );

        subTask.setDescription(request.getDescription());
        log.info("SubTask title with id: {} successfully updated", id);
        return convertToResponse(subTaskRepository.save(subTask));
    }

    public SimpleResponse deleteSubTask(Long id) {
        SubTask subTask = subTaskRepository.findById(id).orElseThrow(() -> {
                    log.error("SubTask with id: {} not found!", id);
                    throw new NoSuchElementException(SubTask.class, id);
                }
        );

        subTask.setEstimation(null);
        subTask.setChecklist(null);
        subTaskRepository.delete(subTask);
        log.info("SubTask with id: {} successfully deleted!", id);
        return new SimpleResponse("Subtask with id " + id + " successfully deleted!", "DELETE");
    }

    public SubTaskResponse addToCompleted(Long subtaskId) {
        SubTask subTask = subTaskRepository.findById(subtaskId).orElseThrow(() -> {
                    log.error("SubTask with id: {} not found!", subtaskId);
                    throw new NoSuchElementException(SubTask.class, subtaskId);
                }
        );

        subTask.setIsDone(true);
        log.info("SubTask with id: {} successfully completed!", subtaskId);
        return convertToResponse(subTaskRepository.save(subTask));
    }

    public SubTaskResponse uncheck(Long subtaskId) {
        SubTask subTask = subTaskRepository.findById(subtaskId).orElseThrow(() -> {
                    log.error("SubTask with id: {} not found!", subtaskId);
                    throw new NoSuchElementException(SubTask.class, subtaskId);
                }
        );

        subTask.setIsDone(false);
        return convertToResponse(subTaskRepository.save(subTask));
    }

    private SubTaskResponse convertToResponse(SubTask subTask) {
        List<MemberResponse> memberResponses = new ArrayList<>();
        EstimationResponse estimationResponse = new EstimationResponse();
        if (subTask.getWorkspacesMembers() == null) {
            if (subTask.getEstimation() == null) {
                return new SubTaskResponse(subTask.getId(), subTask.getDescription(), subTask.getIsDone(), memberResponses, estimationResponse);
            } else {
                return new SubTaskResponse(subTask.getId(), subTask.getDescription(), subTask.getIsDone(), memberResponses,
                        new EstimationResponse(subTask.getEstimation().getId(),
                                subTask.getEstimation().getStartDate(),
                                checklistService.convertStartTimeToResponse(subTask.getEstimation().getStartTime()),
                                subTask.getEstimation().getDueDate(),
                                checklistService.convertStartTimeToResponse(subTask.getEstimation().getDeadlineTime()),
                                subTask.getEstimation().getReminder()));
            }
        } else {
            for (User user : subTask.getWorkspacesMembers()) {
                memberResponses.add(convertToMemberResponse(user));
            }
            if (subTask.getEstimation() != null) {
                return new SubTaskResponse(subTask.getId(), subTask.getDescription(), subTask.getIsDone(), memberResponses,
                        new EstimationResponse(subTask.getEstimation().getId(),
                                subTask.getEstimation().getStartDate(),
                                checklistService.convertStartTimeToResponse(subTask.getEstimation().getStartTime()),
                                subTask.getEstimation().getDueDate(),
                                checklistService.convertStartTimeToResponse(subTask.getEstimation().getDeadlineTime()),
                                subTask.getEstimation().getReminder()));
            } else {
                return new SubTaskResponse(subTask.getId(), subTask.getDescription(), subTask.getIsDone(), memberResponses, estimationResponse);
            }
        }
    }

    private MemberResponse convertToMemberResponse(User user) {
        return new MemberResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getImage()
        );
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();
        return userRepository.findUserByEmail(login).orElseThrow(
                () -> {
                    log.error("User not found!");
                    throw new NoSuchElementException("User not found!");
                }
        );
    }

}
