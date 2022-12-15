package kg.peaksoft.taskTrackerb6.db.repository;

import kg.peaksoft.taskTrackerb6.db.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select c from Comment c where c.card.id = ?1")
    List<Comment> findAllComments(Long id);

    @Transactional
    @Modifying
    @Query("delete from Comment c where c.id = :id")
    void deleteComment(Long id);

    @Query("select c from Comment c where c.card.id = :id order by c.id desc")
    List<Comment> getAllSortedById(Long id);
}