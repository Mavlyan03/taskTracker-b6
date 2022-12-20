package kg.peaksoft.taskTrackerb6.db.repository;

import kg.peaksoft.taskTrackerb6.db.model.Estimation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface EstimationRepository extends JpaRepository<Estimation, Long> {

    @Transactional
    @Modifying
    @Query("delete from Estimation e where e.id = :id")
    void deleteEstimation(Long id);

    @Query("select e from Estimation e where e.card.id = :cardId")
    Estimation findEstimationByCardId(Long cardId);
}