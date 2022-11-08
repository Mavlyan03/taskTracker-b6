package kg.peaksoft.taskTrackerb6.db.repository;

import kg.peaksoft.taskTrackerb6.db.model.SubTask;
import kg.peaksoft.taskTrackerb6.dto.response.SubTaskResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubTaskRepository extends JpaRepository<SubTask, Long> {

    @Query("select new kg.peaksoft.taskTrackerb6.dto.response.SubTaskResponse(s.id, s.description, s.isDone)" +
            " from SubTask s where s.checklist.id = ?1")
    List<SubTaskResponse> getSubTaskResponseByChecklistId(Long checklistId);
}