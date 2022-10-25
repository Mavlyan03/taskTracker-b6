package kg.peaksoft.taskTrackerb6.db.service;

import kg.peaksoft.taskTrackerb6.db.model.User;
import kg.peaksoft.taskTrackerb6.db.model.Workspace;
import kg.peaksoft.taskTrackerb6.db.repository.UserRepository;
import kg.peaksoft.taskTrackerb6.db.repository.WorkspaceRepository;
import kg.peaksoft.taskTrackerb6.dto.request.AdminProfileRequest;
import kg.peaksoft.taskTrackerb6.dto.response.AdminProfileResponse;
import kg.peaksoft.taskTrackerb6.dto.response.ProjectResponse;
import kg.peaksoft.taskTrackerb6.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AdminProfileResponse adminProfile(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("user with id: " + id + " not found!" )
        );

        return new AdminProfileResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhotoLink(),
                getAllProjectResponse());
    }


    private List<ProjectResponse> getAllProjectResponse() {
        List<ProjectResponse> projectResponses = new ArrayList<>();
        List<Workspace> workspaces = workspaceRepository.findAll();
        for (Workspace workspace : workspaces) {
            projectResponses.add(convertToProjectResponse(workspace));
        }
        return projectResponses;
    }

    private ProjectResponse convertToProjectResponse(Workspace workspace) {
        return new ProjectResponse(workspace.getName());
    }

//    public AdminProfileResponse updateAdminProfile(Long id, AdminProfileRequest request) {
//        User user = userRepository.findById(id).orElseThrow(
//                () -> new NotFoundException("user with id: " + id + " not found!")
//        );
//
//        User user1 = updateUserEntity(user, request);
//        return new AdminProfileResponse(
//                user1.getId(),
//                user1.getFirstName(),
//                user1.getLastName(),
//                user1.getEmail(),
//                user1.getPhotoLink(),
//                getAllProjectResponse());
//    }


    @Transactional
    public AdminProfileResponse updateUserEntity(AdminProfileRequest adminProfileRequest) {
        User authenticatedUser = getAuthenticatedUser();
        authenticatedUser.setFirstName(adminProfileRequest.getFirstName());
        authenticatedUser.setLastName(adminProfileRequest.getLastName());
        authenticatedUser.setEmail(adminProfileRequest.getEmail());
        authenticatedUser.setPhotoLink(adminProfileRequest.getPhotoLink());
        authenticatedUser.setPassword(passwordEncoder.encode(adminProfileRequest.getPassword()));
        return new AdminProfileResponse(
                authenticatedUser.getId(),
                authenticatedUser.getFirstName(),
                authenticatedUser.getLastName(),
                authenticatedUser.getEmail(),
                authenticatedUser.getPhotoLink(),
                getAllProjectResponse());
    }


    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();
        return userRepository.findByEmail(login).orElseThrow(() ->
                new NotFoundException("User not found!"));
    }


}
