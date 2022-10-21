package kg.peaksoft.taskTrackerb6.db.service;

import kg.peaksoft.taskTrackerb6.db.model.User;
import kg.peaksoft.taskTrackerb6.db.repository.UserRepository;
import kg.peaksoft.taskTrackerb6.dto.response.AdminProfileResponse;
import kg.peaksoft.taskTrackerb6.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;

    public AdminProfileResponse finAdmin(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new NotFoundException("Not"));
        return new AdminProfileResponse(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail());
    }

    public AdminProfileResponse Abdeit()
}
