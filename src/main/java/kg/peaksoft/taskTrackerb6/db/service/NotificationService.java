package kg.peaksoft.taskTrackerb6.db.service;

import kg.peaksoft.taskTrackerb6.db.model.Notification;
import kg.peaksoft.taskTrackerb6.db.model.User;
import kg.peaksoft.taskTrackerb6.db.repository.NotificationRepository;
import kg.peaksoft.taskTrackerb6.db.repository.UserRepository;
import kg.peaksoft.taskTrackerb6.dto.response.NotificationResponse;
import kg.peaksoft.taskTrackerb6.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

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

    public List<NotificationResponse> getAllMyNotifications() {
        User user = getAuthenticateUser();
        List<NotificationResponse> notificationResponses = new ArrayList<>();
        for (Notification notification : user.getNotifications()) {
            notificationResponses.add(new NotificationResponse(notification));
        }

        log.info("Get all user notifications");
        return notificationResponses;
    }

    public NotificationResponse getById(Long id) {
        Notification notification = notificationRepository.findById(id).get();
        return new NotificationResponse(
                notification.getId(),
                notification.getFromUser().getId(),
                notification.getFromUser().getFirstName(),
                notification.getFromUser().getLastName(),
                notification.getFromUser().getImage(),
                notification.getCreatedAt(),
                notification.getNotificationType(),
                notification.getMessage()
        );
    }
}