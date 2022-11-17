package kg.peaksoft.taskTrackerb6.db.repository;

import kg.peaksoft.taskTrackerb6.db.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u where u.email = :email")
    Optional<User> findUserByEmail(String email);

    @Query("select case when count(u)>0 then true else false end from User u where u.email like :email")
    boolean existsUserByEmail(@Param(value = "email") String email);

    @Query("select u from User u inner join Board b on u.id = b.id")
    List<User> getAllUserFromBoardId(@Param("boardId") Long id);

    @Query("select u from User u where"
            + " upper(u.firstName) like concat('%',:text, '%')"
            + " or lower(u.firstName) like concat('%',:text, '%')"
            + " or initcap(u.firstName) like concat('%',:text, '%')"
            + " or upper(u.lastName) like concat('%',:text, '%')"
            + " or lower(u.lastName) like concat('%',:text, '%')"
            + " or initcap(u.lastName) like concat('%',:text, '%')"
            + " or u.email like :text "
    )
    List<User> globalSearch(@Param("text") String text);

    @Query("select new kg.peaksoft.taskTrackerb6.dto.response.CardMemberResponse(u.id, u.photoLink)" +
            " from User u where u.id = ?1")
    List<User> getAllCardMembers(Long cardId);
}

