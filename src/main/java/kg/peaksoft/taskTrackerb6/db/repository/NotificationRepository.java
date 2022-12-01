package kg.peaksoft.taskTrackerb6.db.repository;

import kg.peaksoft.taskTrackerb6.db.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Transactional
    @Modifying
    @Query("delete from Notification n where n.id = :id")
    void deleteNotification(Long id);

}