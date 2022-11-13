package kg.peaksoft.taskTrackerb6.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kg.peaksoft.taskTrackerb6.db.service.NotificationService;
import kg.peaksoft.taskTrackerb6.dto.response.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.LifecycleState;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Notification API", description = "All endpoints of notification")
public class NotificationApi {

    private final NotificationService notificationService;

    @Operation(summary = "Get user's notification", description = "Get all user's notification")
    @GetMapping
    public List<NotificationResponse> getAllUserNotification() {
        return notificationService.getAllMyNotification();
    }

    @Operation(summary = "Get notification", description = "Get notification by id")
    @GetMapping("{id}")
    public NotificationResponse getNotificationById(@PathVariable Long id) {
        return notificationService.getById(id);
    }

}
