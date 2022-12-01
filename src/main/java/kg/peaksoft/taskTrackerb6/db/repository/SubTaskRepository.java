package kg.peaksoft.taskTrackerb6.db.repository;

import kg.peaksoft.taskTrackerb6.db.model.SubTask;
import kg.peaksoft.taskTrackerb6.dto.response.SubTaskResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface SubTaskRepository extends JpaRepository<SubTask, Long> {

    @Query("select new kg.peaksoft.taskTrackerb6.dto.response.SubTaskResponse(s.id, s.description, s.isDone)" +
            " from SubTask s where s.checklist.id = ?1")
    List<SubTaskResponse> getSubTaskResponseByChecklistId(Long checklistId);

    @Transactional
    @Modifying
    @Query("delete from SubTask s where s.id = :id")
    void deleteSubTask(Long id);

}