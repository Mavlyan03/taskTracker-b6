package kg.peaksoft.taskTrackerb6.db.repository;

import kg.peaksoft.taskTrackerb6.db.model.Workspace;
import kg.peaksoft.taskTrackerb6.dto.response.WorkspaceResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {

    @Transactional
    @Modifying
    @Query("delete from Workspace w where w.id = :id")
    void deleteWorkspaceById(Long id);

    @Query("select new kg.peaksoft.taskTrackerb6.dto.response.WorkspaceResponse(" +
            "w.id, w.name, w.isFavorite) from Workspace w where w.lead.id = ?1")
    List<WorkspaceResponse> getWorkspaceResponse(Long id);
}