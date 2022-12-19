package kg.peaksoft.taskTrackerb6.db.service;

import kg.peaksoft.taskTrackerb6.db.model.*;
import kg.peaksoft.taskTrackerb6.db.repository.*;
import kg.peaksoft.taskTrackerb6.dto.response.AllMemberResponse;
import kg.peaksoft.taskTrackerb6.dto.response.MemberResponse;
import kg.peaksoft.taskTrackerb6.enums.NotificationType;
import kg.peaksoft.taskTrackerb6.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;
    private final CardRepository cardRepository;
    private final BoardRepository boardRepository;
    private final NotificationRepository notificationRepository;

    private User getAuthenticateUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();
        return userRepository.findUserByEmail(login).orElseThrow(
                () -> {
                    log.error("User not found!");
                    throw new NotFoundException("User not found!");
                }
        );
    }

    public List<MemberResponse> searchByEmailOrName(Long id, String emailOrName) {
        Workspace workspace = workspaceRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Workspace with id: " + id + " not found!")
        );

        return userRepository.searchByEmailOrName(emailOrName,workspace.getId());
    }

    public AllMemberResponse getAllMembers(Long id) {
        Card card = cardRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Card with id: " + id + " not found!")
        );

        Board board = boardRepository.findById(card.getColumn().getBoard().getId()).orElseThrow(
                () -> new NotFoundException("Board with id: " + card.getColumn().getBoard().getId() + " not found!")
        );

        Workspace workspace = workspaceRepository.findById(card.getColumn().getBoard().getWorkspace().getId()).orElseThrow(
                () -> new NotFoundException("Workspace with id: " + card.getColumn().getBoard().getWorkspace().getId() + " not found!")
        );

        AllMemberResponse members = new AllMemberResponse();
        List<MemberResponse> boardMembers = new ArrayList<>();
        List<MemberResponse> workspaceMembers = new ArrayList<>();
        for(User user : board.getMembers()) {
            boardMembers.add(new MemberResponse(user));
        }
        for(UserWorkSpace workSpace : workspace.getUserWorkSpaces()) {
            User user = userRepository.findUserByWorkSpaceId(workSpace.getId()).orElseThrow(
                    () -> new NotFoundException("User with id: " + workSpace.getId() + " not found!")
            );

            workspaceMembers.add(new MemberResponse(user));
        }
        members.setBoardMembers(boardMembers);
        members.setWorkspaceMembers(workspaceMembers);
        return members;
    }


    public MemberResponse assignMemberToCard(Long memberId, Long cardId) {
        User authenticateUser = getAuthenticateUser();
        User user = userRepository.findById(memberId).orElseThrow(
                () -> new NotFoundException("User with id: " + memberId + " not found!")
        );

        Card card = cardRepository.findById(cardId).orElseThrow(
                () -> new NotFoundException("Card with id: " + cardId + " not found!")
        );

        card.addMember(user);
        Notification notification = new Notification();
        notification.setCard(card);
        notification.setNotificationType(NotificationType.ASSIGN);
        notification.setFromUser(authenticateUser);
        notification.setUser(user);
        notification.setBoard(card.getColumn().getBoard());
        notification.setCreatedAt(LocalDateTime.now());
        notification.setMessage("You assigned to card: " + card + ", by " + authenticateUser);
        notification.setIsRead(false);
        notificationRepository.save(notification);
        cardRepository.save(card);
        return new MemberResponse(user);
    }
}
