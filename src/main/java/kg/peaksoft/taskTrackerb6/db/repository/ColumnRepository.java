package kg.peaksoft.taskTrackerb6.db.repository;

import kg.peaksoft.taskTrackerb6.db.model.Column;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ColumnRepository extends JpaRepository<Column, Long> {

    @Query("select c from Column c where c.isArchive = false and c.board.id = ?1")
    List<Column> findAllColumns(Long id);

    @Query("select c from Column c where c.isArchive = true")
    List<Column> findAllArchivedColumns();


}