package kg.peaksoft.taskTrackerb6.db.repository;

import kg.peaksoft.taskTrackerb6.db.model.Card;
import kg.peaksoft.taskTrackerb6.dto.response.CardResponse;
import kg.peaksoft.taskTrackerb6.dto.response.CardResponse1;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    @Query("select new kg.peaksoft.taskTrackerb6.dto.response.CardResponse(c.id, c.title) from Card c" +
            " where c.line.id = ?1 and c.isArchive = false")
    List<CardResponse1> findAllCardResponse(Long id);

    @Query("select new kg.peaksoft.taskTrackerb6.dto.response.CardResponse(c.id, c.title) " +
            "from Card c where c.board.id = ?1 and c.isArchive = true ")
    List<CardResponse1> findAllArchivedCards(Long boardId);
}