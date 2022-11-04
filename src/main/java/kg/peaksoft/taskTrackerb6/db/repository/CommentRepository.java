package kg.peaksoft.taskTrackerb6.db.repository;

import kg.peaksoft.taskTrackerb6.db.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

//    @Query("select new kg.peaksoft.taskTrackerb6.dto.response.CommentResponse(c.id, c.text, c.createdDate, c.user.id)" +
//            " from Comment c where c.card.id = ?1")
//    List<CommentResponse> getCommentsByCardId(Long cardId);
}