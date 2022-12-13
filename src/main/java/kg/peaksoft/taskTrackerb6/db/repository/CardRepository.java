package kg.peaksoft.taskTrackerb6.db.repository;

import kg.peaksoft.taskTrackerb6.db.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    @Query("select c from Card c where c.createdAt between :from and :to and c.column.board.workspace.id = :id")
    List<Card> searchCardByCreatedAt(Long id,
                                     @Param("from") LocalDate from,
                                     @Param("to") LocalDate to);

    @Transactional
    @Modifying
    @Query("delete from Card c where c.id = :id")
    void deleteCard(Long id);

    @Query("select c from Card c where c.column.board.workspace.id = :id")
    List<Card> findAllByWorkspaceId(Long id);

    @Query("select c from Card c where c.column.id = :columnId order by c.createdAt desc")
    List<Card> cards(Long columnId);
}