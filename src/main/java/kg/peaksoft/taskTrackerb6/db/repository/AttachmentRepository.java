package kg.peaksoft.taskTrackerb6.db.repository;

import kg.peaksoft.taskTrackerb6.db.model.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    @Transactional
    @Modifying
    @Query("delete from Attachment a where a.id = :id")
    void deleteAttachment(Long id);

    @Query("select a from Attachment a where a.card.id = :cardId")
    List<Attachment> getAllByCardId(Long cardId);

}