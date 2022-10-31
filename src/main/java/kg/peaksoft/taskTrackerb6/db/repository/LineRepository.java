package kg.peaksoft.taskTrackerb6.db.repository;

import kg.peaksoft.taskTrackerb6.db.model.Line;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LineRepository extends JpaRepository<Line, Long> {

    @Query("select l from Line l where l.isArchive = false and l.board.id = ?1")
    List<Line> findAllLines(Long id);

    @Query("select l from Line l where l.isArchive = true")
    List<Line> findAllByIsArchive();
}