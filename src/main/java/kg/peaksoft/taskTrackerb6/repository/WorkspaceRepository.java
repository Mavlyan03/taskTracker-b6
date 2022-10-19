package kg.peaksoft.taskTrackerb6.repository;

import kg.peaksoft.taskTrackerb6.entities.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {

}