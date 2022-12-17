package kg.peaksoft.taskTrackerb6.db.service;

import kg.peaksoft.taskTrackerb6.db.model.Checklist;
import kg.peaksoft.taskTrackerb6.db.model.SubTask;
import kg.peaksoft.taskTrackerb6.db.model.User;
import kg.peaksoft.taskTrackerb6.db.model.Workspace;
import kg.peaksoft.taskTrackerb6.db.repository.ChecklistRepository;
import kg.peaksoft.taskTrackerb6.db.repository.SubTaskRepository;
import kg.peaksoft.taskTrackerb6.db.repository.UserRepository;
import kg.peaksoft.taskTrackerb6.db.repository.WorkspaceRepository;
import kg.peaksoft.taskTrackerb6.dto.request.SubTaskRequest;
import kg.peaksoft.taskTrackerb6.dto.response.SimpleResponse;
import kg.peaksoft.taskTrackerb6.dto.response.SubTaskResponse;
import kg.peaksoft.taskTrackerb6.exceptions.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class SubTaskService {

    private final ChecklistRepository checklistRepository;
    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;
    private final SubTaskRepository subTaskRepository;

    public SubTaskResponse createSubTask(Long id, SubTaskRequest request) {

        User currentUser = getCurrentUser();
        Checklist checklist = checklistRepository.findById(id).orElseThrow(() -> {
                    log.error("Checklist with id: {} not found!", id);
                    throw new NoSuchElementException(Checklist.class, id);
                }
        );

        Workspace workspace = workspaceRepository.findById(checklist.getCard().getColumn().getBoard().getWorkspace().getId()).orElseThrow(
                () -> {
                    log.error("Workspace with id: {} not found!", checklist.getCard().getColumn().getBoard().getWorkspace().getId());
                    throw new NoSuchElementException(Workspace.class, checklist.getCard().getColumn().getBoard().getWorkspace().getId());
                }
        );

        SubTask subTask = new SubTask();
        subTask.setIsDone(request.getIsDone());
        subTask.setDescription(request.getDescription());
        subTask.setChecklist(checklist);
        checklist.addSubTaskToChecklist(subTask);
        log.info("SubTask successfully created");
        SubTask save = subTaskRepository.save(subTask);
        return new SubTaskResponse(
                save.getId(),
                save.getDescription(),
                save.getIsDone()
        );
    }

    public SubTaskResponse updateDescription(Long id, SubTaskRequest request) {
        SubTask subTask = subTaskRepository.findById(id).orElseThrow(() -> {
                    log.error("SubTask with id: {} not found!", id);
                    throw new NoSuchElementException(SubTask.class, id);
                }
        );

        subTask.setDescription(request.getDescription());
        log.info("SubTask title with id: {} successfully updated", id);
        SubTask save = subTaskRepository.save(subTask);
        return new SubTaskResponse(
                save.getId(),
                save.getDescription(),
                save.getIsDone()
        );
    }

    public SimpleResponse deleteSubTask(Long id) {
        SubTask subTask = subTaskRepository.findById(id).orElseThrow(() -> {
                    log.error("SubTask with id: {} not found!", id);
                    throw new NoSuchElementException(SubTask.class, id);
                }
        );

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
        SubTask save = subTaskRepository.save(subTask);
        return new SubTaskResponse(
                save.getId(),
                save.getDescription(),
                save.getIsDone()
        );
    }

    public SubTaskResponse uncheck(Long subtaskId) {
        SubTask subTask = subTaskRepository.findById(subtaskId).orElseThrow(() -> {
                    log.error("SubTask with id: {} not found!", subtaskId);
                    throw new NoSuchElementException(SubTask.class, subtaskId);
                }
        );

        subTask.setIsDone(false);
        SubTask save = subTaskRepository.save(subTask);
        return new SubTaskResponse(
                save.getId(),
                save.getDescription(),
                save.getIsDone()
        );
    }

//    private SubTaskResponse convertToResponse(SubTask subTask) {
//        List<MemberResponse> memberResponses = new ArrayList<>();
//        EstimationResponse estimationResponse = new EstimationResponse();
//        if (subTask.getWorkspacesMembers() == null) {
//            if (subTask.getEstimation() == null) {
//                return new SubTaskResponse(subTask.getId(), subTask.getDescription(), subTask.getIsDone(), memberResponses, estimationResponse);
//            } else {
//                return new SubTaskResponse(subTask.getId(), subTask.getDescription(), subTask.getIsDone(), memberResponses,
//                        new EstimationResponse(subTask.getEstimation().getId(),
//                                subTask.getEstimation().getStartDate(),
//                                checklistService.convertStartTimeToResponse(subTask.getEstimation().getStartTime()),
//                                subTask.getEstimation().getDueDate(),
//                                checklistService.convertStartTimeToResponse(subTask.getEstimation().getDeadlineTime()),
//                                subTask.getEstimation().getReminder()));
//            }
//        } else {
//            for (User user : subTask.getWorkspacesMembers()) {
//                memberResponses.add(convertToMemberResponse(user));
//            }
//            if (subTask.getEstimation() != null) {
//                return new SubTaskResponse(subTask.getId(), subTask.getDescription(), subTask.getIsDone(), memberResponses,
//                        new EstimationResponse(subTask.getEstimation().getId(),
//                                subTask.getEstimation().getStartDate(),
//                                checklistService.convertStartTimeToResponse(subTask.getEstimation().getStartTime()),
//                                subTask.getEstimation().getDueDate(),
//                                checklistService.convertStartTimeToResponse(subTask.getEstimation().getDeadlineTime()),
//                                subTask.getEstimation().getReminder()));
//            } else {
//                return new SubTaskResponse(subTask.getId(), subTask.getDescription(), subTask.getIsDone(), memberResponses, estimationResponse);
//            }
//        }
//    }

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
