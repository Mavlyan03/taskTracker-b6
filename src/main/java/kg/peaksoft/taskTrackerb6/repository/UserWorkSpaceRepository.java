package kg.peaksoft.taskTrackerb6.repository;

import kg.peaksoft.taskTrackerb6.entities.UserWorkSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserWorkSpaceRepository extends JpaRepository<UserWorkSpace, Long> {
}