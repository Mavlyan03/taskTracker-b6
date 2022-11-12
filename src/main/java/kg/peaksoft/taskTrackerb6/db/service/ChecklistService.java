package kg.peaksoft.taskTrackerb6.db.service;

import kg.peaksoft.taskTrackerb6.db.model.Card;
import kg.peaksoft.taskTrackerb6.db.model.Checklist;
import kg.peaksoft.taskTrackerb6.db.model.SubTask;
import kg.peaksoft.taskTrackerb6.db.repository.CardRepository;
import kg.peaksoft.taskTrackerb6.db.repository.ChecklistRepository;
import kg.peaksoft.taskTrackerb6.dto.request.ChecklistRequest;
import kg.peaksoft.taskTrackerb6.dto.request.SubTaskRequest;
import kg.peaksoft.taskTrackerb6.dto.response.ChecklistResponse;
import kg.peaksoft.taskTrackerb6.dto.response.SubTaskResponse;
import kg.peaksoft.taskTrackerb6.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChecklistService {

    private final CardRepository cardRepository;
    private final ChecklistRepository checklistRepository;

    public ChecklistResponse createChecklist(Long id, ChecklistRequest request){
        Card card = cardRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Card with id: "+id+" not found!")
        );
        Checklist checklist = new Checklist();
        checklist.setTitle(request.getTitle());
        checklist.setCard(card);
        List<SubTask> subTasks = new ArrayList<>();
        for (SubTaskRequest subTaskRequest : request.getSubTaskRequests()) {
            subTasks.add(new SubTask(subTaskRequest.getDescription(), subTaskRequest.getIsDone()));
        }
        checklist.setSubTasks(subTasks);
        checklist.setCount(request.getCount());
        return convertToResponse(checklistRepository.save(checklist));
    }

    public ChecklistResponse convertToResponse(Checklist checklist){
        List<SubTask> allSubTasks = checklist.getSubTasks();
        List<SubTaskResponse> subTaskResponses = new ArrayList<>();

        int countOfSubTasks = 0;
        int countOfCompletedSubTask = 0;
        if (allSubTasks == null){
            countOfSubTasks = 0;
            countOfCompletedSubTask = 0;
        }else {
            List<Boolean> subTasks = new ArrayList<>();
            for (SubTask subTask : allSubTasks) {
                countOfSubTasks++;
                if (subTask.getIsDone() == true){
                    subTasks.add(subTask.getIsDone());
                    countOfCompletedSubTask++;
                }
            }
            for (SubTask subTask : allSubTasks) {
                subTaskResponses.add(new SubTaskResponse(subTask.getId(), subTask.getDescription(), subTask.getIsDone()));
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
        return new ChecklistResponse(checklist.getId(), checklist.getTitle(),
                                     countOfCompletedSubTask, countOfSubTasks,
                                     checklist.getCount(), subTaskResponses);
    }
}
