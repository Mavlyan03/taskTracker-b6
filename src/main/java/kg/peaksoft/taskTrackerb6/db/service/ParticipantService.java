package kg.peaksoft.taskTrackerb6.db.service;


import kg.peaksoft.taskTrackerb6.db.model.Board;
import kg.peaksoft.taskTrackerb6.db.model.User;
import kg.peaksoft.taskTrackerb6.db.model.UserWorkSpace;
import kg.peaksoft.taskTrackerb6.db.model.Workspace;
import kg.peaksoft.taskTrackerb6.db.repository.*;
import kg.peaksoft.taskTrackerb6.dto.request.InviteRequest;
import kg.peaksoft.taskTrackerb6.dto.response.ParticipantResponse;
import kg.peaksoft.taskTrackerb6.dto.response.SimpleResponse;
import kg.peaksoft.taskTrackerb6.exceptions.BadCredentialException;
import kg.peaksoft.taskTrackerb6.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ParticipantService {

    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;
    private final BoardRepository boardRepository;
    private final JavaMailSender mailSender;

    private User getAuthenticateUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();
        return userRepository.findUserByEmail(login).orElseThrow(() -> {
                    log.error("User not found!");
                    throw new NotFoundException("User not found!");
                }
        );
    }

    public ParticipantResponse mapToResponse(User user) {
        ParticipantResponse participantResponse = new ParticipantResponse();
        participantResponse.setId(user.getId());
        participantResponse.setFirstName(user.getFirstName());
        participantResponse.setLastName(user.getLastName());
        participantResponse.setEmail(user.getEmail());
        participantResponse.setRole(user.getRole());
        return participantResponse;
    }

    public SimpleResponse deleteParticipantFromWorkspace(Long userId, Long workspaceId) {
        User killer = getAuthenticateUser();

        Workspace workspace = workspaceRepository.findById(workspaceId).orElseThrow(
                () -> {
                    log.error("Workspace with id: {} not found!", workspaceId);
                    throw new NotFoundException("Workspace with id: " + workspaceId + " not found!");
                }
        );

        User corpse = userRepository.findById(userId).orElseThrow(
                () -> {
                    log.error("User with id: {} not found!", userId);
                    throw new NotFoundException("User with id: " + userId + " not found");
                }
        );

        if (corpse.equals(killer)) {
            log.error("You can not remove yourself!");
            throw new BadCredentialException("You can not remove yourself!");
        }

        if (workspace.getLead().equals(corpse)) {
            log.error("This user is lead of workspace, you can not delete!");
            throw new BadCredentialsException("This user is lead of workspace, you can not delete!");
        }

        List<User> workspaceUsers = new ArrayList<>();
        for (UserWorkSpace userWorkSpace : workspace.getUserWorkSpaces()) {
            workspaceUsers.add(userWorkSpace.getUser());
        }

        workspaceUsers.remove(corpse);
        log.info("User with id: " + userId + " successfully deleted from workspace with id: {} ", workspaceId);
        return new SimpleResponse("User successfully deleted from workspace!", "DELETE");
    }

    public SimpleResponse deleteParticipantFromBoard(Long id, Long boardId) {
        User user = userRepository.findById(id).orElseThrow(
                () -> {
                    log.error("User with id: {} not found!", id);
                    throw new NotFoundException("User with  id" + id + " not found!");
                }
        );

        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> {
                    log.error("Board with id: {} not found!", boardId);
                    throw new NotFoundException("Board with id" + boardId + " not found!");
                }
        );

        board.getMembers().remove(user);
        user.setBoards(null);
        log.info("User with id: " + id + "successfully deleted from board with id: {}", boardId);
        return new SimpleResponse("User successfully deleted from board!", "DELETE");
    }

    public List<ParticipantResponse> getAllParticipantFromBoard(Long boardId) {
        List<ParticipantResponse> participantResponse = new ArrayList<>();
        for (User user1 : userRepository.getAllUserFromBoardId(boardId)) {
            participantResponse.add(mapToResponse(user1));
        }

        log.info("Get all participant from board");
        return participantResponse;
    }

    public List<ParticipantResponse> getAllParticipantFromWorkspace(Long workspaceId) {
        Workspace workspace = workspaceRepository.findById(workspaceId).orElseThrow(
                () -> {
                    log.error("Workspace with id: {} not found!", workspaceId);
                    throw new NotFoundException("Workspace with id " + workspaceId + " not found!");
                }
        );

        List<User> members = new ArrayList<>();
        for (UserWorkSpace w : workspace.getUserWorkSpaces()) {
            members.add(w.getUser());
        }

        List<ParticipantResponse> participantResponses = new ArrayList<>();
        for (User member : members) {
            participantResponses.add(new ParticipantResponse(member));
        }

        log.info("Get all participant from workspace");
        return participantResponses;
    }

    public SimpleResponse inviteParticipant(InviteRequest request) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setSubject("[task_tracker] registry new member");
        helper.setTo(request.getEmail());
        if (request.getRole().toString().equals("ADMIN")) {
            helper.setText(request.getLink() + request.getRole());
        }
        helper.setText(request.getLink() + request.getRole());
        mailSender.send(mimeMessage);
        return new SimpleResponse("Email send", "ok");
    }
}