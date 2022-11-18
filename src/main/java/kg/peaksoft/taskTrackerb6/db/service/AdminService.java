package kg.peaksoft.taskTrackerb6.db.service;

import kg.peaksoft.taskTrackerb6.db.model.User;
import kg.peaksoft.taskTrackerb6.db.model.Workspace;
import kg.peaksoft.taskTrackerb6.db.repository.UserRepository;
import kg.peaksoft.taskTrackerb6.db.repository.WorkspaceRepository;
import kg.peaksoft.taskTrackerb6.dto.request.AdminProfileRequest;
import kg.peaksoft.taskTrackerb6.dto.response.ProfileResponse;
import kg.peaksoft.taskTrackerb6.dto.response.ProjectResponse;
import kg.peaksoft.taskTrackerb6.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public ProfileResponse getProfile() {
        User user = getAuthenticatedUser();
        return new ProfileResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getImage(),
                getAllProjectResponse());
    }


    private List<ProjectResponse> getAllProjectResponse() {
        List<ProjectResponse> projectResponses = new ArrayList<>();
        List<Workspace> workspaces = workspaceRepository.findAll();
        for (Workspace workspace : workspaces) {
            projectResponses.add(convertToProjectResponse(workspace));
        }
        log.info("AllRroject");
        return projectResponses;
    }

    private ProjectResponse convertToProjectResponse(Workspace workspace) {
        return new ProjectResponse(workspace.getName());
    }

    public ProfileResponse updateUserEntity(AdminProfileRequest adminProfileRequest) {
        User authenticatedUser = getAuthenticatedUser();
        authenticatedUser.setFirstName(adminProfileRequest.getFirstName());
        authenticatedUser.setLastName(adminProfileRequest.getLastName());
        authenticatedUser.setEmail(adminProfileRequest.getEmail());
        if (adminProfileRequest.getImage() == null) {
            authenticatedUser.setImage(authenticatedUser.getImage());
        }

        authenticatedUser.setImage(adminProfileRequest.getImage());
        authenticatedUser.setPassword(passwordEncoder.encode(adminProfileRequest.getPassword()));
        return new ProfileResponse(
                authenticatedUser.getId(),
                authenticatedUser.getFirstName(),
                authenticatedUser.getLastName(),
                authenticatedUser.getEmail(),
                authenticatedUser.getImage(),
                getAllProjectResponse());
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();
        return userRepository.findUserByEmail(login).orElseThrow(() ->
                new NotFoundException("User not found!"));
    }
        return userRepository.findByEmail(login).orElseThrow(() -> {log.error("User not found!");

                throw  new NotFoundException("User not found!");
    });
}
}