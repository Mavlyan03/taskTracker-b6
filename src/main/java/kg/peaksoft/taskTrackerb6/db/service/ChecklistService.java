package kg.peaksoft.taskTrackerb6.db.service;

import kg.peaksoft.taskTrackerb6.db.converter.CardConverter;
import kg.peaksoft.taskTrackerb6.db.model.Card;
import kg.peaksoft.taskTrackerb6.db.model.Checklist;
import kg.peaksoft.taskTrackerb6.db.model.SubTask;
import kg.peaksoft.taskTrackerb6.db.repository.CardRepository;
import kg.peaksoft.taskTrackerb6.db.repository.ChecklistRepository;
import kg.peaksoft.taskTrackerb6.dto.request.ChecklistRequest;
import kg.peaksoft.taskTrackerb6.dto.request.SubTaskRequest;
import kg.peaksoft.taskTrackerb6.dto.request.UpdateChecklistTitleRequest;
import kg.peaksoft.taskTrackerb6.dto.response.ChecklistResponse;
import kg.peaksoft.taskTrackerb6.dto.response.SimpleResponse;
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
        checklist.setCount(request.getCount());
        List<SubTask> subTasks = new ArrayList<>();
        for (SubTaskRequest subTaskRequest : request.getSubTaskRequests()) {
            SubTask subTask = new SubTask(subTaskRequest.getDescription(), subTaskRequest.getIsDone());
            subTask.setChecklist(checklist);
            checklist.addSubTaskToChecklist(subTask);
            subTasks.add(subTask);
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
        checklistRepository.delete(checklist);
        return new SimpleResponse("Checklist with id "+id+" cucessfully deleted", "DELETED");
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
            countOfSubTasks = 0;
            countOfCompletedSubTask = 0;
            return new ChecklistResponse(checklist.getId(), checklist.getTitle(),
                    countOfCompletedSubTask, countOfSubTasks,
                    checklist.getCount(), subTaskResponses);
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
}
