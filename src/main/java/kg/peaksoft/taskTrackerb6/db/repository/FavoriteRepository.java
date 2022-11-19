package kg.peaksoft.taskTrackerb6.db.repository;

import kg.peaksoft.taskTrackerb6.db.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    @Modifying
    @Query("delete from Favorite f where f.id = ?1")
    void deleteFavorite(Long id);
}