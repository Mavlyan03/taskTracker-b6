package kg.peaksoft.taskTrackerb6.db.repository;

import kg.peaksoft.taskTrackerb6.db.model.User;
import kg.peaksoft.taskTrackerb6.dto.response.CreatorResponse;
import kg.peaksoft.taskTrackerb6.dto.response.MemberResponse;
import kg.peaksoft.taskTrackerb6.dto.response.ParticipantResponse;
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

    @Query("select new kg.peaksoft.taskTrackerb6.dto.response.CardMemberResponse(u.id, u.image)" +
            " from User u where u.id = ?1")
    List<User> getAllCardMembers(Long cardId);

    @Query("select new kg.peaksoft.taskTrackerb6.dto.response.CreatorResponse(" +
            "u.id, " +
            "u.firstName, " +
            "u.lastName, " +
            "u.image) from User u where u.id = :id")
    CreatorResponse getCreatorResponse(Long id);

    @Query("select new kg.peaksoft.taskTrackerb6.dto.response.MemberResponse(" +
            "u.id, " +
            "u.firstName, " +
            "u.lastName, " +
            "u.email," +
            "u.image, " +
            "u.role) from User u join UserWorkSpace w " +
            "on w.workspace.id = :id and w.user.id = u.id where concat(u.firstName,' ',u.lastName,' ',u.email) like concat('%',:email,'%') ")
    List<MemberResponse> searchByEmailOrName(@Param("email") String email, @Param("id") Long id);

    @Query("select u from User u join UserWorkSpace w on w.user.id = u.id where w.id = ?1")
    Optional<User> findUserByWorkSpaceId(Long id);

    @Query("select new kg.peaksoft.taskTrackerb6.dto.response.ParticipantResponse(" +
            "u.id, " +
            "u.firstName, " +
            "u.lastName, " +
            "u.email, " +
            "u.image, " +
            "u.role) from User u where u.id = :id")
    ParticipantResponse getParticipant(Long id);
}

