package kg.peaksoft.taskTrackerb6.db.service;

import kg.peaksoft.taskTrackerb6.db.model.*;
import kg.peaksoft.taskTrackerb6.db.repository.*;
import kg.peaksoft.taskTrackerb6.dto.request.UpdateRequest;
import kg.peaksoft.taskTrackerb6.dto.request.WorkspaceRequest;
import kg.peaksoft.taskTrackerb6.dto.response.SimpleResponse;
import kg.peaksoft.taskTrackerb6.dto.response.BoardResponse;
import kg.peaksoft.taskTrackerb6.dto.response.WorkspaceInnerPageResponse;
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
    private final BoardRepository boardRepository;
    private final CardRepository cardRepository;
    private final ColumnRepository columnRepository;
    private final EstimationRepository estimationRepository;
    private final NotificationRepository notificationRepository;
    private final ChecklistRepository checklistRepository;
    private final SubTaskRepository subTaskRepository;
    private final AttachmentRepository attachmentRepository;
    private final CommentRepository commentRepository;
    private final BasketRepository basketRepository;


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


    public WorkspaceResponse createWorkspace(WorkspaceRequest request) throws MessagingException {
        User user = getAuthenticateUser();
        Workspace workspace = new Workspace();
        workspace.setName(request.getName());
        workspace.setIsFavorite(workspace.getIsFavorite());
        UserWorkSpace userWorkSpace = new UserWorkSpace(user, workspace, Role.ADMIN);
        user.addUserWorkSpace(userWorkSpace);
        workspace.addUserWorkSpace(userWorkSpace);
        workspace.setLead(user);
        Workspace savedWorkspace = workspaceRepository.save(workspace);
        userWorkSpaceRepository.save(userWorkSpace);
        if (request.getEmails().isEmpty() || request.getEmails().get(0).equals("") || request.getEmails().get(0).isBlank()) {

        } else {
            for (String email : request.getEmails()) {
                boolean exists = userRepository.existsUserByEmail(email);
                if (!exists) {
                    MimeMessage mimeMessage = mailSender.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                    helper.setSubject("Hello, welcome to my workspace! To join click on the link!");
                    helper.setFrom("tasktracker.b6@gmail.com");
                    helper.setTo(email);
                    helper.setText(request.getLink() + "/" + Role.ADMIN + "/workspaceId/" + workspace.getId());
                    mailSender.send(mimeMessage);
                } else {
                    User inviteMember = userRepository.findUserByEmail(email).orElseThrow(
                            () -> new NotFoundException("User with email: " + email + " not found!")
                    );

                    UserWorkSpace member = new UserWorkSpace(inviteMember, workspace, Role.ADMIN);
                    userWorkSpaceRepository.save(member);
                }
            }
        }

        log.info("Workspace successfully created");
        return new WorkspaceResponse(
                savedWorkspace.getId(),
                savedWorkspace.getName(),
                userRepository.getCreatorResponse(savedWorkspace.getLead().getId()),
                savedWorkspace.getIsFavorite()
        );
    }


    public WorkspaceInnerPageResponse getById(Long id) {
        Workspace workspace = workspaceRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Workspace with id: " + id + " not found!")
        );

        User user = getAuthenticateUser();
        List<Favorite> favorites = user.getFavorites();
        List<Board> workspaceBoards = workspace.getBoards();
        List<Board> userFavoriteBoards = new ArrayList<>();
        List<BoardResponse> boardResponses = new ArrayList<>();
        for (Favorite fav : favorites) {
            if (fav.getBoard() != null) {
                userFavoriteBoards.add(fav.getBoard());
            }
        }

        for (Board board : workspaceBoards) {
            if (userFavoriteBoards.contains(board)) {
                for (Board favBoard : userFavoriteBoards) {
                    if (favBoard.equals(board)) {
                        boardResponses.add(new BoardResponse(
                                board.getId(),
                                board.getTitle(),
                                true,
                                board.getBackground(),
                                workspace.getId())
                        );
                    }
                }
            } else {
                boardResponses.add(new BoardResponse(
                        board.getId(),
                        board.getTitle(),
                        false,
                        board.getBackground(),
                        workspace.getId())
                );
            }
        }

        return new WorkspaceInnerPageResponse(
                workspace.getId(),
                workspace.getName(),
                boardResponses
        );
    }


    @Transactional
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

        List<Basket> baskets = basketRepository.findAll();
        for (Board b : workspace.getBoards()) {
            List<Column> columns = columnRepository.findAllColumnsByBoardId(b.getId());
            if (columns != null) {
                for (Column column : columns) {
                    for (Card card : cardRepository.findCardsByColumnId(column.getId())) {
                        for (Basket basket : baskets) {
                            if (basket.getCard() != null && basket.getCard().equals(card)) {
                                basketRepository.deleteBasket(basket.getId());
                            }
                            if (basket.getColumn() != null && basket.getColumn().equals(column)) {
                                basketRepository.deleteBasket(basket.getId());
                            }
                        }


                        for (Attachment attachment : attachmentRepository.getAllByCardId(card.getId())) {
                            attachmentRepository.deleteAttachment(attachment.getId());
                        }

                        for (Checklist c : checklistRepository.findAllChecklists(card.getId())) {
                            for (SubTask s : c.getSubTasks()) {
                                subTaskRepository.deleteSubTask(s.getId());
                            }

                            checklistRepository.deleteChecklist(c.getId());
                        }

                        Estimation estimation = estimationRepository.findEstimationByCardId(card.getId());
                        if (estimation != null) {
                            Notification notification = notificationRepository.findNotification(estimation.getId());
                            if (notification != null) {
                                notificationRepository.deleteNotification(notification.getId());
                            }

                            estimationRepository.deleteEstimation(estimation.getId());
                        }

                        for (Comment comment : commentRepository.findAllCommentsByCardId(card.getId())) {
                            commentRepository.deleteComment(comment.getId());
                        }

                        card.setLabels(null);

                        List<Notification> cardNotification = notificationRepository.findAllByCardId(card.getId());
                        if (cardNotification != null) {
                            for (Notification n : cardNotification) {
                                notificationRepository.deleteNotification(n.getId());
                            }
                        }

                        cardRepository.deleteCard(card.getId());
                    }

                    List<Notification> columnNotifications = notificationRepository.findAllByColumnId(column.getId());
                    if (columnNotifications != null) {
                        for (Notification notification : columnNotifications) {
                            notificationRepository.deleteNotification(notification.getId());
                        }
                    }

                    for (Basket basket : baskets) {
                        for (Card c : cardRepository.findCardsByColumnId(column.getId())) {
                            if (basket.getCard() != null && basket.getCard().equals(c)) {
                                c.setIsArchive(false);
                                basketRepository.deleteBasket(basket.getId());
                            }
                        }

                        if (basket.getColumn() != null && basket.getColumn().equals(column)) {
                            basketRepository.deleteBasket(basket.getId());
                        }
                    }

                    columnRepository.deleteColumn(column.getId());
                }
            }

            List<Notification> boarNotifications = notificationRepository.findAllByBoardId(b.getId());
            if (boarNotifications != null) {
                for (Notification n : boarNotifications) {
                    notificationRepository.deleteNotification(n.getId());
                }
            }

            List<Favorite> allFavorites = favoriteRepository.findAll();
            if (allFavorites.contains(b.getFavorite())) {
                for (Favorite fav : allFavorites) {
                    if (fav.getBoard() != null) {
                        if (fav.getBoard().equals(b)) {
                            favoriteRepository.deleteFavorite(fav.getId());
                        }
                    }
                }
            }

            boardRepository.deleteBoard(b.getId());
        }

        List<Favorite> allFavorites = favoriteRepository.findAll();
        if (allFavorites.contains(workspace.getFavorite())) {
            for (Favorite fav : allFavorites) {
                if (fav.getWorkspace() != null) {
                    if (fav.getWorkspace().equals(workspace)) {
                        favoriteRepository.deleteFavorite(fav.getId());
                    }
                }
            }
        }

        List<UserWorkSpace> workSpaces = workspace.getUserWorkSpaces();
        for (UserWorkSpace userWorkspace : workSpaces) {
            if (userWorkspace.getWorkspace().equals(workspace)) {
                userWorkSpaceRepository.deleteUserWorkSpace(userWorkspace.getId());
            }
        }

        workspaceRepository.deleteWorkspaceById(workspace.getId());
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
                    favoriteRepository.deleteFavorite(fav.getId());
                    log.info("Favorite is deleted!");
                    log.info("Workspace favorite with id: {} successfully changed to false", workspace.getId());
                    return new WorkspaceResponse(
                            workspace.getId(),
                            workspace.getName(),
                            userRepository.getCreatorResponse(workspace.getLead().getId()),
                            false
                    );
                }
            }
        }

        Favorite favorite = new Favorite(user, workspace);
        favoriteRepository.save(favorite);
        user.addFavorite(favorite);
        log.info("Workspace action with id: {} successfully changed to true", id);
        log.info("Favorite is saved!");
        return new WorkspaceResponse(
                workspace.getId(),
                workspace.getName(),
                userRepository.getCreatorResponse(workspace.getLead().getId()),
                true
        );
    }


    public List<WorkspaceResponse> getAllUserWorkspaces() {
        User user = getAuthenticateUser();
        List<Workspace> workspaces = new ArrayList<>();
        List<Workspace> workspacesRepositoryGetAll = workspaceRepository.getAllUserWorkspaces();
        List<UserWorkSpace> userWorkSpaces = user.getUserWorkSpaces();
        for (UserWorkSpace userWorkSpace : userWorkSpaces) {
            for (Workspace w : workspacesRepositoryGetAll) {
                if (userWorkSpace.getWorkspace().equals(w)) {
                    workspaces.add(userWorkSpace.getWorkspace());

                }
            }
        }

        List<Workspace> favoriteWorkspaces = new ArrayList<>();
        List<Favorite> userFavorites = user.getFavorites();
        for (Favorite fav : userFavorites) {
            if (fav.getWorkspace() != null) {
                favoriteWorkspaces.add(fav.getWorkspace());
            }
        }

        List<WorkspaceResponse> workspaceResponses = new ArrayList<>();
        for (Workspace workspace : workspaces) {
            if (favoriteWorkspaces.contains(workspace)) {
                for (Workspace w : favoriteWorkspaces) {
                    if (w.equals(workspace)) {
                        workspaceResponses.add(new WorkspaceResponse(
                                workspace.getId(),
                                workspace.getName(),
                                userRepository.getCreatorResponse(workspace.getLead().getId()),
                                true)
                        );
                    }
                }
            } else {
                workspaceResponses.add(new WorkspaceResponse(
                        workspace.getId(),
                        workspace.getName(),
                        userRepository.getCreatorResponse(workspace.getLead().getId()),
                        false)
                );
            }
        }

        log.info("Get all user workspaces");
        return workspaceResponses;
    }


    public WorkspaceInnerPageResponse updateWorkspaceName(UpdateRequest request) {
        User user = getAuthenticateUser();
        Workspace workspace = workspaceRepository.findById(request.getId()).orElseThrow(
                () -> new NotFoundException("Workspace with id: " + request.getId() + " not found!")
        );

        if (!workspace.getLead().equals(user)) {
            throw new BadCredentialException("You can not update this workspace name!");
        }

        workspace.setName(request.getNewTitle());
        Workspace saved = workspaceRepository.save(workspace);

        List<Favorite> favorites = user.getFavorites();
        List<Board> workspaceBoards = workspace.getBoards();
        List<Board> userFavoriteBoards = new ArrayList<>();
        List<BoardResponse> boardResponses = new ArrayList<>();
        for (Favorite fav : favorites) {
            if (fav.getBoard() != null) {
                userFavoriteBoards.add(fav.getBoard());
            }
        }

        for (Board board : workspaceBoards) {
            if (userFavoriteBoards.contains(board)) {
                for (Board favBoard : userFavoriteBoards) {
                    if (favBoard.equals(board)) {
                        boardResponses.add(new BoardResponse(
                                board.getId(),
                                board.getTitle(),
                                true,
                                board.getBackground(),
                                workspace.getId())
                        );
                    }
                }
            } else {
                boardResponses.add(new BoardResponse(
                        board.getId(),
                        board.getTitle(),
                        false,
                        board.getBackground(),
                        workspace.getId())
                );
            }
        }

        return new WorkspaceInnerPageResponse(
                saved.getId(),
                saved.getName(),
                boardResponses
        );
    }
}