package kg.peaksoft.taskTrackerb6.db.service;

import kg.peaksoft.taskTrackerb6.db.model.Board;
import kg.peaksoft.taskTrackerb6.db.model.User;
import kg.peaksoft.taskTrackerb6.db.model.UserWorkSpace;
import kg.peaksoft.taskTrackerb6.db.model.Workspace;
import kg.peaksoft.taskTrackerb6.db.repository.*;
import kg.peaksoft.taskTrackerb6.dto.request.InviteRequest;
import kg.peaksoft.taskTrackerb6.dto.response.ParticipantResponse;
import kg.peaksoft.taskTrackerb6.dto.response.SimpleResponse;
import kg.peaksoft.taskTrackerb6.enums.Role;
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
    private final UserWorkSpaceRepository userWorkSpaceRepository;

    private User getAuthenticateUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();
        return userRepository.findUserByEmail(login).orElseThrow(() -> {
                    log.error("User not found!");
                    throw new NotFoundException("User not found!");
                }
        );
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
        List<UserWorkSpace> userWorkSpaces = workspace.getUserWorkSpaces();
        for (UserWorkSpace userWorkSpace : userWorkSpaces) {
            workspaceUsers.add(userWorkSpace.getUser());
        }

        Long deleteId = null;
        for (UserWorkSpace w : userWorkSpaces) {
            if (w.getWorkspace().equals(workspace)) {
                if (w.getUser().equals(corpse)) {
                    deleteId = w.getId();
                }
            }
        }

        userWorkSpaceRepository.deleteUserWorkSpace(deleteId);
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
        log.info("User with id: " + id + "successfully deleted from board with id: {}", boardId);
        return new SimpleResponse("User successfully deleted from board!", "DELETE");
    }

    public List<ParticipantResponse> getAllParticipantFromBoard(Long boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow(
                () -> new NotFoundException("Board with id: " + boardId + " not found!")
        );

        List<ParticipantResponse> participantResponse = new ArrayList<>();
        for (User user1 : board.getMembers()) {
            participantResponse.add(userRepository.getParticipant(user1.getId()));
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

    public SimpleResponse inviteNewParticipantToBoard(InviteRequest request) throws MessagingException {
        User user;
        if (!userRepository.existsUserByEmail(request.getEmail())) {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setSubject("[task_tracker] invite new member to board!");
            helper.setTo(request.getEmail());
            Board board = boardRepository.findById(request.getWorkspaceOrBoardId()).orElseThrow(
                    () -> new NotFoundException("Board with id: " + request.getWorkspaceOrBoardId() + " not found!")
            );

            Workspace workspace = workspaceRepository.findById(board.getWorkspace().getId()).orElseThrow(
                    () -> new NotFoundException("Workspace with id: " + board.getWorkspace().getId() + " not found!")
            );

            if (request.getRole().equals(Role.ADMIN)) {
                helper.setText(request.getLink() + "/" + request.getRole() + "/workspaceId/" + workspace.getId() + "/boardId/" + request.getWorkspaceOrBoardId());
            } else if (request.getRole().equals(Role.USER)) {
                helper.setText(request.getLink() + "/" + request.getRole() + "/workspaceId/" + workspace.getId() + "/boardId/" + request.getWorkspaceOrBoardId());
            }
        if (request.getRole().equals(Role.ADMIN)) {
            helper.setText(request.getLink() + "/" + request.getRole() + "/workspaceId/" + workspace.getId() + " /boardId/" + request.getWorkspaceOrBoardId());
        } else if (request.getRole().equals(Role.USER)) {
            helper.setText(request.getLink() + "/" + request.getRole() + "/workspaceId/" + workspace.getId() + "/boardId/" + request.getWorkspaceOrBoardId());
        }

            mailSender.send(mimeMessage);
        } else {
            user = userRepository.findUserByEmail(request.getEmail()).orElseThrow(
                    () -> new NotFoundException("User with email: " + request.getEmail() + " not found!")
            );

            Board board = boardRepository.findById(request.getWorkspaceOrBoardId()).orElseThrow(
                    () -> new NotFoundException("Board with id: " + request.getWorkspaceOrBoardId() + " not found!")
            );

            Workspace workspace = workspaceRepository.findById(board.getWorkspace().getId()).orElseThrow(
                    () -> new NotFoundException("Workspace with id: " + board.getWorkspace().getId() + " not found!")
            );

            board.addMember(user);
            UserWorkSpace userWorkSpace = new UserWorkSpace(user, workspace, request.getRole());
            userWorkSpaceRepository.save(userWorkSpace);
        }

        return new SimpleResponse("Email send!", "OK");
    }

    public SimpleResponse inviteNewParticipantToWorkspace(InviteRequest request) throws MessagingException {
        User user;
        if (!userRepository.existsUserByEmail(request.getEmail())) {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setSubject("[task_tracker] invite new member to workspace!");
            helper.setTo(request.getEmail());

            if (request.getRole().equals(Role.ADMIN)) {
                helper.setText(request.getLink() + "/" + request.getRole() + "/workspaceId/" + request.getWorkspaceOrBoardId());
            } else if (request.getRole().equals(Role.USER)) {
                helper.setText(request.getLink() + "/" + request.getRole() + "/workspaceId/" + request.getWorkspaceOrBoardId());
            }

            mailSender.send(mimeMessage);
        } else {
            user = userRepository.findUserByEmail(request.getEmail()).orElseThrow(
                    () -> new NotFoundException("User with email: " + request.getEmail() + " not found!")
            );

            Workspace workspace = workspaceRepository.findById(request.getWorkspaceOrBoardId()).orElseThrow(
                    () -> new NotFoundException("Workspace with id: " + request.getWorkspaceOrBoardId() + " not found!")
            );

            UserWorkSpace userWorkSpace = new UserWorkSpace(user, workspace, request.getRole());
            userWorkSpaceRepository.save(userWorkSpace);
        }

        return new SimpleResponse("Email send!", "OK");
    }
}
