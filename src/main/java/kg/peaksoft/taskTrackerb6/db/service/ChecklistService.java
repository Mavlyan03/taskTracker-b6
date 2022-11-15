package kg.peaksoft.taskTrackerb6.db.service;

import kg.peaksoft.taskTrackerb6.db.model.*;
import kg.peaksoft.taskTrackerb6.db.repository.*;
import kg.peaksoft.taskTrackerb6.dto.request.*;
import kg.peaksoft.taskTrackerb6.dto.response.*;
import kg.peaksoft.taskTrackerb6.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChecklistService {

    private final CardRepository cardRepository;
    private final ChecklistRepository checklistRepository;
    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;
    private final BoardRepository boardRepository;

    public ChecklistResponse createChecklist(Long id, ChecklistRequest request){
        User authUser = getAuthenticateUser();
        Card card = cardRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Card with id: "+id+" not found!")
        );
        Board board = boardRepository.findById(card.getBoard().getId()).get();
        Workspace workspace = workspaceRepository.findById(board.getWorkspace().getId()).get();

        Checklist checklist = new Checklist();
        checklist.setTitle(request.getTitle());
        List<MemberResponse> members = new ArrayList<>();
        for (UserWorkSpace userWorkSpace : workspace.getUserWorkSpaces()) {
            if (!authUser.equals(userWorkSpace.getUser())){
                members.add(convertToMemberResponse(userWorkSpace.getUser()));
            }
        }
        for (SubTaskRequest subTaskRequest : request.getSubTaskRequests()) {
            SubTask subTask = new SubTask(subTaskRequest.getDescription(), subTaskRequest.getIsDone());
            for (MemberResponse memberResponse : members) {
                for (MemberRequest memberRequest : subTaskRequest.getMemberRequests()) {
                    if (memberResponse.getEmail().equals(memberRequest.getEmail())){
                        subTask.addMember(convertMemberToUser(memberRequest));
                    }
                }
            }
            subTask.setChecklist(checklist);
            checklist.addSubTaskToChecklist(subTask);
            if (subTaskRequest.getEstimationRequest() != null){
                Estimation estimation = new Estimation();
                    estimation.setStartDate(subTaskRequest.getEstimationRequest().getStartDate());
                    estimation.setDueDate(subTaskRequest.getEstimationRequest().getDueDate());
                    estimation.setReminder(subTaskRequest.getEstimationRequest().getReminder());
                    estimation.setStartTime(convertTimeToEntity(subTaskRequest.getEstimationRequest().getStartTime()));
                    estimation.setDeadlineTime(convertTimeToEntity(subTaskRequest.getEstimationRequest().getDeadlineTime()));
                    estimation.setUser(authUser);
                    subTask.setEstimation(estimation);
                    estimation.setSubTask(subTask);
            }
        }
        checklist.setCard(card);
        card.addChecklist(checklist);
        return convertToResponse(checklistRepository.save(checklist));
    }

    public ChecklistResponse updateTitle(UpdateChecklistTitleRequest request){
        Checklist checklist = checklistRepository.findById(request.getChecklistId()).orElseThrow(
                ()-> new NotFoundException("Checklist with id: "+request.getChecklistId()+" not found!")
        );
        checklist.setTitle(request.getNewTitle());
        return convertToResponse(checklistRepository.save(checklist));
    }

    public SimpleResponse deleteChecklist(Long id){
        Checklist checklist = checklistRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Checklist with id: "+id+" not found!")
        );
        List<SubTask> subTasks = new ArrayList<>();
        for (SubTask subTask : checklist.getSubTasks()) {
            subTask.setEstimation(null);
            Notification notification = new Notification();
            if (notification.getSubTask().getId().equals(subTask.getId())){
                notification.setSubTask(null);
            }
            subTasks.add(subTask);
        }
        checklist.setSubTasks(subTasks);

        checklistRepository.delete(checklist);
        return new SimpleResponse("Checklist with id "+id+" successfully deleted", "DELETED");
    }

    public List<ChecklistResponse> findAllChecklistsByCardId(Long id){
        List<Checklist> checklists = checklistRepository.findAllChecklists(id);
        List<ChecklistResponse> checklistResponses = new ArrayList<>();
        for (Checklist checklist : checklists) {
            checklistResponses.add(convertToResponse(checklist));
        }
        return checklistResponses;
    }

    public ChecklistResponse convertToResponse(Checklist checklist){
        List<SubTask> allSubTasks = new ArrayList<>();
        if (checklist.getSubTasks() != null){
            allSubTasks = checklist.getSubTasks();
        }
        List<SubTaskResponse> subTaskResponses = new ArrayList<>();

        int countOfSubTasks = 0;
        int countOfCompletedSubTask = 0;
        if (allSubTasks == null){
            return new ChecklistResponse(checklist.getId(), checklist.getTitle(),
                                         countOfCompletedSubTask, countOfSubTasks,
                                         checklist.getCount(), subTaskResponses);
        }else {
            for (SubTask subTask : allSubTasks) {
                countOfSubTasks++;
                if (subTask.getIsDone().equals(true)){
                    countOfCompletedSubTask++;
                }
            }
            for (SubTask subTask : allSubTasks) {
                List<MemberResponse> memberResponses = new ArrayList<>();
                EstimationResponse estimationResponse = new EstimationResponse();
                if (subTask.getWorkspacesUsers() == null){
                    if (subTask.getEstimation() == null){
                        subTaskResponses.add(new SubTaskResponse(subTask.getId(), subTask.getDescription(), subTask.getIsDone(),
                                                                 memberResponses, estimationResponse));
                    }else {
                        subTaskResponses.add(new SubTaskResponse(subTask.getId(), subTask.getDescription(), subTask.getIsDone(),
                                                                 memberResponses, new EstimationResponse(subTask.getEstimation().getId(),
                                                                        subTask.getEstimation().getStartDate(),
                                                                        convertStartTimeToResponse(subTask.getEstimation().getStartTime()),
                                                                        subTask.getEstimation().getDueDate(),
                                                                        convertStartTimeToResponse(subTask.getEstimation().getDeadlineTime()),
                                                                        subTask.getEstimation().getReminder())));
                    }
                }else {
                    for (User user : subTask.getWorkspacesUsers()) {
                        memberResponses.add(convertToMemberResponse(user));
                    }
                    if (subTask.getEstimation() != null){
                        subTaskResponses.add(new SubTaskResponse(subTask.getId(), subTask.getDescription(), subTask.getIsDone(),
                                                                 memberResponses, new EstimationResponse(subTask.getEstimation().getId(),
                                                                                                         subTask.getEstimation().getStartDate(),
                                                                                                         convertStartTimeToResponse(subTask.getEstimation().getStartTime()),
                                                                                                         subTask.getEstimation().getDueDate(),
                                                                                                         convertStartTimeToResponse(subTask.getEstimation().getDeadlineTime()),
                                                                                                         subTask.getEstimation().getReminder())));
                    }else {
                        subTaskResponses.add(new SubTaskResponse(subTask.getId(), subTask.getDescription(), subTask.getIsDone(),
                                                                 memberResponses, estimationResponse));
                    }
                }
            }
        Integer count;
        if (countOfCompletedSubTask <= 0){
            count = 0;
        }else {
            count = (countOfCompletedSubTask * 100) / countOfSubTasks;
        }
        checklist.setCount(count);
        checklistRepository.save(checklist);
        return new ChecklistResponse(checklist.getId(), checklist.getTitle(), countOfCompletedSubTask,
                                     countOfSubTasks, checklist.getCount(), subTaskResponses);
        }
    }

    public MemberResponse convertToMemberResponse(User user){
        return new MemberResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhotoLink()
        );
    }

    public MyTimeClassResponse convertStartTimeToResponse(MyTimeClass timeClass){
        return new MyTimeClassResponse(timeClass.getId(), String.format("%02d:%02d", timeClass.getHour(), timeClass.getMinute()));
    }
    public User getAuthenticateUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();
        return userRepository.findByEmail(login).orElseThrow(
                ()-> new NotFoundException("User not found!")
        );
    }

    public User convertMemberToUser(MemberRequest memberRequest){
        return userRepository.findByEmail(memberRequest.getEmail()).get();
    }

    public MyTimeClass convertTimeToEntity(MyTimeClassRequest request){
        MyTimeClass myTimeClass = new MyTimeClass();
        myTimeClass.setTime(request.getHour(), request.getMinute());
        return myTimeClass;
    }
}
