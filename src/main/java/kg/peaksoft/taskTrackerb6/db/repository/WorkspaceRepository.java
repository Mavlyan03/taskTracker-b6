package kg.peaksoft.taskTrackerb6.db.repository;

import kg.peaksoft.taskTrackerb6.db.model.User;
import kg.peaksoft.taskTrackerb6.db.model.Workspace;
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

    @Query("select distinct w from Workspace w join UserWorkSpace s on w.id = s.workspace.id order by w.id")
    List<Workspace> getAllUserWorkspaces();

//    @Query("select distinct w from Workspace w join UserWorkSpace s on s.user = ?1 order by w.id")
//    List<Workspace> getAllUserWorkspaces(User user);
}