package kg.peaksoft.taskTrackerb6.db.service;

import kg.peaksoft.taskTrackerb6.db.model.Board;
import kg.peaksoft.taskTrackerb6.db.model.User;
import kg.peaksoft.taskTrackerb6.db.model.UserWorkSpace;
import kg.peaksoft.taskTrackerb6.db.model.Workspace;
import kg.peaksoft.taskTrackerb6.db.repository.BoardRepository;
import kg.peaksoft.taskTrackerb6.db.repository.UserRepository;
import kg.peaksoft.taskTrackerb6.db.repository.UserWorkSpaceRepository;
import kg.peaksoft.taskTrackerb6.db.repository.WorkspaceRepository;
import kg.peaksoft.taskTrackerb6.dto.request.WorkspaceRequest;
import kg.peaksoft.taskTrackerb6.dto.response.*;
import kg.peaksoft.taskTrackerb6.enums.Role;
import kg.peaksoft.taskTrackerb6.exceptions.BadCredentialException;
import kg.peaksoft.taskTrackerb6.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final UserRepository userRepository;
    private final UserWorkSpaceRepository userWorkSpaceRepository;
    private final JavaMailSender mailSender;
    private final BoardRepository boardRepository;


    public WorkspaceResponse createWorkspace(WorkspaceRequest workspaceRequest, User user) throws MessagingException {
        Workspace workspace = convertToEntity(workspaceRequest);
        User user1 = userRepository.findByEmail(user.getEmail()).orElseThrow(
                () -> new NotFoundException("user with email: " + user.getEmail() + " not found!")
        );

        for (String email : workspaceRequest.getEmails()) {
            boolean exists = userRepository.existsByEmail(email);
            if (!exists) {
                inviteMember(email, workspaceRequest.getLink());
            }
            inviteMember(email, workspaceRequest.getLink());
        }

        UserWorkSpace userWorkSpace = new UserWorkSpace();
        userWorkSpace.setUser(user1);
        userWorkSpace.setWorkspace(workspace);
        userWorkSpace.setRole(Role.ADMIN);
        userWorkSpaceRepository.save(userWorkSpace);
        workspace.setUserWorkSpace(userWorkSpace);
        workspace.setLead(user1);
        user.addWorkspace(workspace);
        return convertToResponse(workspaceRepository.save(workspace));
    }


    public WorkspaceResponse getWorkspaceById(Long id) {
        Workspace workspace = workspaceRepository.findById(id).orElseThrow(
                () -> new NotFoundException("workspace with id: " + id + " not found!")
        );

        return convertToResponse(workspace);
    }


    public SimpleResponse deleteWorkspaceById(Long id, User user) {
        Workspace workspace = workspaceRepository.findById(id).orElseThrow(
                () -> new NotFoundException("workspace with id: " + id + " not found!")
        );

        User user1 = userRepository.findByEmail(user.getEmail()).orElseThrow(
                () -> new NotFoundException("user with email: " + user.getEmail() + " not found!")
        );

        if (!user1.getEmail().equals(workspace.getLead().getEmail())) {
            throw new BadCredentialException("You can not delete this workspace!");
        }

        workspace.setLead(null);

        workspaceRepository.delete(workspace);
        return new SimpleResponse(
                "workspace with id: " + id + " is deleted!",
                "DELETE"
        );
    }


    public WorkspaceResponse changeWorkspacesAction(Long id) {
        Workspace workspace = workspaceRepository.findById(id).orElseThrow(
                () -> new NotFoundException("workspace with id: " + id + " not found!")
        );

        workspace.setIsFavorite(!workspace.getIsFavorite());
        Workspace workspace1 = workspaceRepository.save(workspace);
        return convertToResponse(workspace1);
    }


    public List<WorkspaceResponse> getAllUserWorkspaces(User user) {
        User user1 = userRepository.findByEmail(user.getEmail()).orElseThrow(
                () -> new NotFoundException("user with email: " + user.getEmail() + " not found!")
        );

        List<WorkspaceResponse> workspaceResponses = new ArrayList<>();
        List<Workspace> workspaces = user1.getWorkspaces();
        for (Workspace workspace : workspaces) {
            workspaceResponses.add(convertToResponse(workspace));
        }
        return workspaceResponses;
    }


    public List<FavoritesResponse> getAllFavorites() {
        List<FavoritesResponse> getFavorites = new ArrayList<>();
        getFavorites.add(new FavoritesResponse(getFavoriteWorkspacesList(), getFavoriteBoardsList()));
        return getFavorites;
    }


    private List<FavoriteWorkspaceResponse> getFavoriteWorkspacesList() {
        List<FavoriteWorkspaceResponse> favoriteWorkspaces = new ArrayList<>();
        List<Workspace> workspaces = workspaceRepository.findAllByFavorites();
        for (Workspace workspace : workspaces) {
            favoriteWorkspaces.add(convertToFavoriteWorkspaceResponse(workspace));
        }
        return favoriteWorkspaces;
    }


    private List<FavoriteBoardResponse> getFavoriteBoardsList() {
        List<FavoriteBoardResponse> favoriteBoards = new ArrayList<>();
        List<Board> boards = boardRepository.findAllByFavorites();
        for (Board board : boards) {
            favoriteBoards.add(convertToFavoriteBoardResponse(board));
        }
        return favoriteBoards;
    }


    private void inviteMember(String email, String registrationLink) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setSubject("[Task tracker] invitation");
        helper.setFrom("tasktracker.b6@gmail.com");
        helper.setTo(email);
        helper.setText(registrationLink);
        mailSender.send(mimeMessage);
        new SimpleResponse("mail send", "OK");
    }


    private Workspace convertToEntity(WorkspaceRequest request) {
        Workspace workspace = new Workspace();
        workspace.setName(request.getName());
        workspace.setIsFavorite(workspace.getIsFavorite());
        return workspace;
    }


    private WorkspaceResponse convertToResponse(Workspace workspace) {
        return new WorkspaceResponse(
                workspace.getId(),
                workspace.getName(),
                convertToResponseCreator(workspace.getLead()),
                workspace.getIsFavorite()
        );
    }


    private CreatorResponse convertToResponseCreator(User user) {
        CreatorResponse creatorResponse = new CreatorResponse();
        creatorResponse.setId(user.getId());
        creatorResponse.setFirstName(user.getFirstName());
        creatorResponse.setLastName(user.getLastName());
        creatorResponse.setPhoto(user.getPhotoLink());
        return creatorResponse;
    }


    private FavoriteWorkspaceResponse convertToFavoriteWorkspaceResponse(Workspace workspace) {
        return new FavoriteWorkspaceResponse(
                workspace.getId(),
                workspace.getName(),
                workspace.getIsFavorite()
        );
    }


    private FavoriteBoardResponse convertToFavoriteBoardResponse(Board board) {
        return new FavoriteBoardResponse(
                board.getId(),
                board.getTitle(),
                board.getPhotoLink(),
                board.isFavorite(),
        );
    }

}