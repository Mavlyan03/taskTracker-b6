package kg.peaksoft.taskTrackerb6.db.repository;

import kg.peaksoft.taskTrackerb6.db.model.User;
import kg.peaksoft.taskTrackerb6.dto.response.ParticipantResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u where u.email = :email")
    Optional<User> findByEmail(String email);

    @Query("select case when count(u)>0 then true else false end from User u where u.email like :email")
    boolean existsByEmail(@Param(value = "email") String email);

    @Query("select u from User u inner join Board b on u.id=b.id")
    List<User> getAllUserFromBoardId(@Param("boardId") Long boardId);


    @Query("select u from  User u join Workspace w on u.id=:id")
    List<User> getAllUserFromWorkspace(@Param("workspace") Long workspaceId);
}

//    SELECT Parts.Part, Categories.Catnumb AS Cat, Categories.Price
//        FROM Parts FULL OUTER JOIN Categories
//        ON Parts.Cat = Categories.Catnumb