package kg.peaksoft.taskTrackerb6.db.repository;

import kg.peaksoft.taskTrackerb6.db.model.UserWorkSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface UserWorkSpaceRepository extends JpaRepository<UserWorkSpace, Long> {

    @Transactional
    @Modifying
    @Query("delete from UserWorkSpace u where u.id = :id")
    void deleteUserWorkSpace(Long id);
}