package kg.peaksoft.taskTrackerb6.db.service;

import kg.peaksoft.taskTrackerb6.db.model.User;
import kg.peaksoft.taskTrackerb6.db.repository.UserRepository;
import kg.peaksoft.taskTrackerb6.dto.request.ProfileRequest;
import kg.peaksoft.taskTrackerb6.dto.response.AdminProfileResponse;
import kg.peaksoft.taskTrackerb6.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;

//    public AdminProfileResponse AdminProfileResponsefinAdmin(Authentication authentication) {
//        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new NotFoundException("Not"));
//        return new AdminProfileResponse(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail());
//    }

//    public User convertToEntity(ProfileRequest profileRequest){
//        User user = new User();
//        user.setFirstName(profileRequest.getFirstName());
//        user.setLastName(profileRequest.getLastName());
//        user.setPhotoLink(profileRequest.getPhotoLink());
//        user.setEmail(profileRequest.getEmail());
//        user.setPassword(profileRequest.getPassword());
//        return user;
//    }

    public AdminProfileResponse userProfile(Long id) {
        AdminProfileResponse adminProfileResponse = new AdminProfileResponse();
        User user = userRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format("user with id %s not found", id))
        );
        adminProfileResponse.setUserId(user.getId());
        adminProfileResponse.setFirstName(user.getFirstName());
        adminProfileResponse.setLastName(user.getLastName());
        adminProfileResponse.setPhoto(user.getPhotoLink());
        adminProfileResponse.setEmail(user.getEmail());

    }
}
