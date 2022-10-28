package kg.peaksoft.taskTrackerb6.db.service;


import kg.peaksoft.taskTrackerb6.db.model.Board;
import kg.peaksoft.taskTrackerb6.db.model.User;
import kg.peaksoft.taskTrackerb6.db.model.UserWorkSpace;
import kg.peaksoft.taskTrackerb6.db.model.Workspace;
import kg.peaksoft.taskTrackerb6.db.repository.*;
import kg.peaksoft.taskTrackerb6.dto.response.ParticipantResponse;
import kg.peaksoft.taskTrackerb6.dto.response.SimpleResponse;
import kg.peaksoft.taskTrackerb6.exceptions.BadCredentialException;
import kg.peaksoft.taskTrackerb6.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;

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
        return userRepository.findByEmail(login).orElseThrow(() ->
                new NotFoundException("User not found!"));
    }

    public ParticipantResponse mapToResponse(User user) {
        ParticipantResponse participantResponse = new ParticipantResponse();
        participantResponse.setFirstName(user.getFirstName());
        participantResponse.setLastName(user.getLastName());
        participantResponse.setEmail(user.getEmail());
        participantResponse.setRole(user.getRole());
        return participantResponse;
    }

    public SimpleResponse deleteParticipantFromWorkspace(Long userId, Long workspaceId) {
        User killer = getAuthenticateUser();

        Workspace workspace = workspaceRepository.findById(workspaceId).orElseThrow(
                () -> new NotFoundException(" workspace with this id" + workspaceId + "not found")
        );

        User corpse = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with this id " + userId + " not found")
        );

        if (corpse.equals(killer)) {
            throw new BadCredentialException("You can not remove yourself!");
        }

        if (workspace.getLead().equals(corpse)) {
            throw new BadCredentialsException("This user is lead of workspace, you can not delete!");
        }

        List<User> workspaceUsers = new ArrayList<>();
        for (UserWorkSpace userWorkSpace : workspace.getUserWorkSpaces()) {
            workspaceUsers.add(userWorkSpace.getUser());
        }

        workspaceUsers.remove(corpse);
        return new SimpleResponse("deleted", "ok");
    }

    public SimpleResponse deleteParticipantFromBoard(Long id, Long boardId) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User with  id" + id + " not found"));
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new NotFoundException("Board with id" + boardId + " not found"));
        board.getMembers().remove(user);
        user.setBoards(null);
        return new SimpleResponse("deleted", "ok");
    }

    public List<ParticipantResponse> getAllParticipantFromBoard(Long boardId) {
        List<ParticipantResponse> participantResponse = new ArrayList<>();
        for (User user1 : userRepository.getAllUserFromBoardId(boardId)) {
            participantResponse.add(mapToResponse(user1));
        }

        return participantResponse;
    }

    public List<ParticipantResponse> getAllParticipantFromWorkspace(Long workspaceId) {
        Workspace workspace = workspaceRepository.findById(workspaceId).orElseThrow(
                () -> new NotFoundException("Workspace with id " + workspaceId + " not found")
        );

        List<User> members = new ArrayList<>();
        for (UserWorkSpace w : workspace.getUserWorkSpaces()) {
            members.add(w.getUser());
        }

        List<ParticipantResponse> participantResponses = new ArrayList<>();
        for (User member : members) {
            participantResponses.add(new ParticipantResponse(member));
        }
        return participantResponses;
    }

    public SimpleResponse inviteParticipant(String email, String link) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setSubject("[task_tracker] registry new member");
        helper.setTo(email);
        helper.setText(link, true);
        mailSender.send(mimeMessage);
        return new SimpleResponse("Email send", "ok");
    }
}



