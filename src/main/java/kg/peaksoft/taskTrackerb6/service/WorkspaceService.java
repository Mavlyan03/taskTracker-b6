package kg.peaksoft.taskTrackerb6.service;

import kg.peaksoft.taskTrackerb6.dto.converter.WorkspaceConverter;
import kg.peaksoft.taskTrackerb6.dto.request.WorkspaceRequest;
import kg.peaksoft.taskTrackerb6.dto.response.SimpleResponse;
import kg.peaksoft.taskTrackerb6.dto.response.WorkspaceResponse;
import kg.peaksoft.taskTrackerb6.entities.User;
import kg.peaksoft.taskTrackerb6.entities.UserWorkSpace;
import kg.peaksoft.taskTrackerb6.entities.Workspace;
import kg.peaksoft.taskTrackerb6.enums.Role;
import kg.peaksoft.taskTrackerb6.exceptions.BadCredentialException;
import kg.peaksoft.taskTrackerb6.exceptions.NotFoundException;
import kg.peaksoft.taskTrackerb6.repository.UserRepository;
import kg.peaksoft.taskTrackerb6.repository.UserWorkSpaceRepository;
import kg.peaksoft.taskTrackerb6.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final UserRepository userRepository;
    private final WorkspaceConverter converter;
    private final UserWorkSpaceRepository userWorkSpaceRepository;
    private final JavaMailSender mailSender;

    public WorkspaceResponse createWorkspace(WorkspaceRequest workspaceRequest, User user) throws MessagingException {
        Workspace workspace = converter.convertToEntity(workspaceRequest);
        User user1 = userRepository.findByEmail(user.getEmail()).orElseThrow(
                () -> new NotFoundException(
                        "user with email: " + user.getEmail() + " not found!"
                )
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
        return converter.convertToResponse(workspaceRepository.save(workspace));
    }


    public WorkspaceResponse getWorkspaceById(Long id) {
        Workspace workspace = workspaceRepository.findById(id).orElseThrow(
                () -> new NotFoundException(
                        "workspace with id: " + id + " not found!"
                )
        );

        return converter.convertToResponse(workspace);
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


    public List<WorkspaceResponse> getAllWorkspace() {
        List<WorkspaceResponse> workspaceResponses = new ArrayList<>();
        List<Workspace> workspaces = workspaceRepository.findAll();
        for (Workspace workspace : workspaces) {
            workspaceResponses.add(converter.convertToResponse(workspace));
        }

        return workspaceResponses;
    }


    private SimpleResponse inviteMember(String email, String registrationLink) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setSubject("[Task tracker] invitation");
        helper.setFrom("tasktracker.b6@gmail.com");
        helper.setTo(email);
        helper.setText(registrationLink);
        mailSender.send(mimeMessage);
        return new SimpleResponse("mail send", "OK");
    }

}