package kg.peaksoft.taskTrackerb6.db.repository;

import kg.peaksoft.taskTrackerb6.db.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    @Query("select c from Card c where c.createdAt between :fromDate and :to")
    List<Card> searchCardByCreatedAt(@Param("fromDate") LocalDate fromDate,
                             @Param("to") LocalDate to);
}