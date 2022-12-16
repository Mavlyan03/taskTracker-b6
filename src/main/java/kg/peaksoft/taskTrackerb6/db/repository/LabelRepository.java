package kg.peaksoft.taskTrackerb6.db.repository;

import kg.peaksoft.taskTrackerb6.db.model.Label;
import kg.peaksoft.taskTrackerb6.dto.response.LabelResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface LabelRepository extends JpaRepository<Label, Long> {

//    @Query("select new kg.peaksoft.taskTrackerb6.dto.response.LabelResponse(l.id, l.description, l.color)" +
//            " from Label l " +
//            "join Card c on l.id.c.id = :cardId")
//    List<LabelResponse> getAllLabelResponses(Long cardId);

    @Transactional
    @Modifying
    @Query("delete from Label l where l.id = :id")
    void deleteLabel(Long id);

    @Query("select new kg.peaksoft.taskTrackerb6.dto.response.LabelResponse(" +
            "l.id, " +
            "l.description, " +
            "l.color) from Label l where l.id = :id")
    LabelResponse getLabelResponse(Long id);
}