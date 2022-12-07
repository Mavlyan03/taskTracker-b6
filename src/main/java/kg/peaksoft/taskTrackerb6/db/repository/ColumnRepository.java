package kg.peaksoft.taskTrackerb6.db.repository;

import kg.peaksoft.taskTrackerb6.db.model.Column;
import kg.peaksoft.taskTrackerb6.dto.response.ColumnResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface ColumnRepository extends JpaRepository<Column, Long> {

    @Query("select c from Column c where c.isArchive = false and c.board.id = ?1")
    List<Column> findAllColumns(Long id);

    @Transactional
    @Modifying
    @Query("delete from Column c where c.id = :id")
    void deleteColumn(Long id);
}