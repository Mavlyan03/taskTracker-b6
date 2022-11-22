package kg.peaksoft.taskTrackerb6.db.service;


import kg.peaksoft.taskTrackerb6.db.model.*;
import kg.peaksoft.taskTrackerb6.db.repository.FavoriteRepository;
import kg.peaksoft.taskTrackerb6.db.repository.UserRepository;
import kg.peaksoft.taskTrackerb6.db.repository.UserWorkSpaceRepository;
import kg.peaksoft.taskTrackerb6.db.repository.WorkspaceRepository;
import kg.peaksoft.taskTrackerb6.dto.request.WorkspaceRequest;
import kg.peaksoft.taskTrackerb6.dto.response.SimpleResponse;
import kg.peaksoft.taskTrackerb6.dto.response.WorkspaceResponse;
import kg.peaksoft.taskTrackerb6.enums.Role;
import kg.peaksoft.taskTrackerb6.exceptions.BadCredentialException;
import kg.peaksoft.taskTrackerb6.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final UserRepository userRepository;
    private final UserWorkSpaceRepository userWorkSpaceRepository;
    private final FavoriteRepository favoriteRepository;
    private final JavaMailSender mailSender;

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

    public WorkspaceResponse createWorkspace(WorkspaceRequest workspaceRequest) throws MessagingException {
        User user = getAuthenticateUser();
        Workspace workspace = convertToEntity(workspaceRequest);
        UserWorkSpace userWorkSpace = new UserWorkSpace(user, workspace, Role.ADMIN);
        user.addUserWorkSpace(userWorkSpace);
        workspace.addUserWorkSpace(userWorkSpace);
        workspace.setLead(user);
        userWorkSpaceRepository.save(userWorkSpace);
        Workspace savedWorkspace = workspaceRepository.save(workspace);
        log.info("Workspace successfully created");
        return new WorkspaceResponse(
                savedWorkspace.getId(),
                savedWorkspace.getName(),
                userRepository.getCreatorResponse(savedWorkspace.getLead().getId()),
                savedWorkspace.getIsFavorite());
    }


    public WorkspaceResponse getWorkspaceById(Long id) {
        Workspace workspace = workspaceRepository.findById(id).orElseThrow(
                () -> {
                    log.error("Workspace with id: {} not found!", id);
                    throw new NotFoundException("Workspace with id: " + id + " not found!");
                }
        );

        return new WorkspaceResponse(
                workspace.getId(),
                workspace.getName(),
                userRepository.getCreatorResponse(workspace.getLead().getId()),
                workspace.getIsFavorite()
        );
    }

    public SimpleResponse deleteWorkspaceById(Long id) {
        User user = getAuthenticateUser();

        Workspace workspace = workspaceRepository.findById(id).orElseThrow(
                () -> {
                    log.error("Workspace with id: {} not found!", id);
                    throw new NotFoundException("Workspace with id: " + id + " not found!");
                }
        );

        if (!user.getEmail().equals(workspace.getLead().getEmail())) {
            log.error("You can not delete this workspace!");
            throw new BadCredentialException("You can not delete this workspace!");
        }

        workspaceRepository.delete(workspace);
        log.info("Workspace with id: {} successfully deleted!", id);
        return new SimpleResponse("Workspace with id: " + id + " successfully!", "DELETE");
    }

    public WorkspaceResponse makeFavorite(Long id) {
        User user = getAuthenticateUser();
        Workspace workspace = workspaceRepository.findById(id).orElseThrow(
                () -> {
                    log.error("Workspace with id: {} not found!", id);
                    throw new NotFoundException("Workspace with id: " + id + " not found!");
                }
        );

        List<Favorite> favorites = user.getFavorites();
        for (Favorite fav : favorites) {
            if (fav.getWorkspace() != null) {
                if (fav.getWorkspace().equals(workspace)) {
                    favoriteRepository.delete(fav);
                    favorites.remove(fav);
                    return new WorkspaceResponse(
                            workspace.getId(),
                            workspace.getName(),
                            userRepository.getCreatorResponse(workspace.getLead().getId()),
                            false
                    );
                }
            }
        }

        workspace.setIsFavorite(!workspace.getIsFavorite());
        Workspace workspace1 = workspaceRepository.save(workspace);
        log.info("Workspace action with id: {} successfully change", id);
        Favorite favorite = new Favorite(user, workspace);
        favoriteRepository.save(favorite);
        user.addFavorite(favorite);
        return new WorkspaceResponse(
                workspace.getId(),
                workspace.getName(),
                userRepository.getCreatorResponse(workspace.getLead().getId()),
                true
        );
    }


    public List<WorkspaceResponse> getAllUserWorkspaces() {
        User user = getAuthenticateUser();
        List<WorkspaceResponse> workspaceResponses = new ArrayList<>();
        List<Workspace> workspaces = new ArrayList<>();
        for (UserWorkSpace userWorkSpace : user.getUserWorkSpaces()) {
            if (userWorkSpace.getUser().equals(user)) {
                workspaces.add(userWorkSpace.getWorkspace());
            }
        }

        for (Workspace workspace : workspaces) {
            workspaceResponses.add(new WorkspaceResponse(
                            workspace.getId(),
                            workspace.getName(),
                            userRepository.getCreatorResponse(workspace.getLead().getId()),
                            workspace.getIsFavorite()
                    )
            );
        }

        log.info("Get all workspaces");
        return workspaceResponses;
    }


    private Workspace convertToEntity(WorkspaceRequest request) throws MessagingException {
        Workspace workspace = new Workspace();
        workspace.setName(request.getName());
        workspace.setIsFavorite(workspace.getIsFavorite());

        if (request.getEmails().isEmpty() || request.getEmails().get(0).equals("") || request.getEmails().get(0).isBlank()) {

        } else {
            for (String email : request.getEmails()) {
                boolean exists = userRepository.existsUserByEmail(email);
                if (!exists) {
                    MimeMessage mimeMessage = mailSender.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                    helper.setSubject("[Task tracker] invitation to my workspace");
                    helper.setFrom("tasktracker.b6@gmail.com");
                    helper.setTo(email);
                    helper.setText(request.getLink());
                    mailSender.send(mimeMessage);
                } else {
                    MimeMessage mimeMessage = mailSender.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                    helper.setSubject("[Task tracker] invitation to my workspace");
                    helper.setFrom("tasktracker.b6@gmail.com");
                    helper.setTo(email);
                    helper.setText(request.getLink());
                    mailSender.send(mimeMessage);
                }
            }
        }

        return workspace;
    }
}