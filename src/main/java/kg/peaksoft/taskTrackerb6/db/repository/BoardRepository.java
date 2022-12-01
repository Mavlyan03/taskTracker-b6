package kg.peaksoft.taskTrackerb6.db.repository;

import kg.peaksoft.taskTrackerb6.db.model.Board;
import kg.peaksoft.taskTrackerb6.dto.response.BoardResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("select b from Board b where b.isFavorite = true")
    List<Board> findAllByFavorites();

    @Query("select b from Board b where b.isArchive = true")
    List<Board> findAllByIsArchive();

    @Query("select new kg.peaksoft.taskTrackerb6.dto.response.BoardResponse(" +
            "b.id," +
            "b.title," +
            "b.isFavorite," +
            "b.background) from Board b where b.workspace.id = ?1 and b.isArchive = false")
    List<BoardResponse> findAllBoards(Long id);

    @Transactional
    @Modifying
    @Query("delete from Board b where b.id = :id")
    void deleteBoard(Long id);
}