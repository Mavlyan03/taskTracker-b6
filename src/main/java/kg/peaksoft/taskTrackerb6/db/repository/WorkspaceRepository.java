package kg.peaksoft.taskTrackerb6.db.repository;

import kg.peaksoft.taskTrackerb6.db.model.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {

//    @Modifying
//    @Transactional
//    @Query("update Workspace " +
//            "set members = null where id = ?1")
//    void deleteParticipantFromWorkspace(Long id);
}
