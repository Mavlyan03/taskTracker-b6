package kg.peaksoft.taskTrackerb6.db.service;

import kg.peaksoft.taskTrackerb6.db.model.User;
import kg.peaksoft.taskTrackerb6.db.model.UserWorkSpace;
import kg.peaksoft.taskTrackerb6.db.model.Workspace;
import kg.peaksoft.taskTrackerb6.db.repository.UserRepository;
import kg.peaksoft.taskTrackerb6.dto.request.AdminProfileRequest;
import kg.peaksoft.taskTrackerb6.dto.request.UpdateProfileRequest;
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
    private final BCryptPasswordEncoder passwordEncoder;

    public ProfileResponse getProfileById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Profile with id: " + id + " not found!")
        );

        return new ProfileResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getImage(),
                getAllProjectResponse()
        );
    }

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
        User user = getAuthenticatedUser();
        List<ProjectResponse> projectResponses = new ArrayList<>();
        List<UserWorkSpace> userWorkSpaces = user.getUserWorkSpaces();
        List<Workspace> workspaces = new ArrayList<>();
        for (UserWorkSpace w : userWorkSpaces) {
            workspaces.add(w.getWorkspace());
        }

        for (Workspace workspace : workspaces) {
            projectResponses.add(convertToProjectResponse(workspace));
        }
        log.info("Get all user's workspaces");
        return projectResponses;
    }

    private ProjectResponse convertToProjectResponse(Workspace workspace) {
        return new ProjectResponse(workspace.getName());
    }

    public ProfileResponse updateUserEntity(UpdateProfileRequest request) {
        User authenticatedUser = getAuthenticatedUser();
        authenticatedUser.setFirstName(request.getFirstName());
        authenticatedUser.setLastName(request.getLastName());
        authenticatedUser.setEmail(request.getEmail());
        authenticatedUser.setImage(authenticatedUser.getImage());
        authenticatedUser.setPassword(passwordEncoder.encode(request.getPassword()));
        User save = userRepository.save(authenticatedUser);
        log.info("User profile successfully updated!");
        return new ProfileResponse(
                save.getId(),
                save.getFirstName(),
                save.getLastName(),
                save.getEmail(),
                save.getImage(),
                getAllProjectResponse());
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();
        return userRepository.findUserByEmail(login).orElseThrow(
                () -> {
                    log.error("User with email: {} not found!", login);
                    throw new NotFoundException("User not found!");
                }
        );
    }
}