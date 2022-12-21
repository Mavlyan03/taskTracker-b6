package kg.peaksoft.taskTrackerb6.db.repository;

import kg.peaksoft.taskTrackerb6.db.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Transactional
    @Modifying
    @Query("delete from Notification n where n.id = :id")
    void deleteNotification(Long id);

    @Query("select n from Notification n where n.board.id = :id")
    List<Notification> findAllByBoardId(Long id);

    @Query("select n from Notification n where n.card.id = :id")
    List<Notification> findAllByCardId(Long id);

    @Query("select n from Notification n where n.column.id = :id")
    List<Notification> findAllByColumnId(Long id);

    @Query("select n from Notification n where n.estimation.id = :id")
    Notification findNotification(Long id);
}