package kg.peaksoft.taskTrackerb6.db.repository;

import kg.peaksoft.taskTrackerb6.db.model.Estimation;
import kg.peaksoft.taskTrackerb6.dto.response.EstimationResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EstimationRepository extends JpaRepository<Estimation, Long> {

    @Query("select new kg.peaksoft.taskTrackerb6.dto.response.EstimationResponse(e.id, e.createdDate, e.deadlineDate, e.reminder)" +
            " from Estimation e where e.card.id = ?1")
    EstimationResponse getEstimationByCardId(Long cardId);
}