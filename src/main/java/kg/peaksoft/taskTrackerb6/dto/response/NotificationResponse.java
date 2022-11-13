package kg.peaksoft.taskTrackerb6.dto.response;

import kg.peaksoft.taskTrackerb6.db.model.Notification;
import kg.peaksoft.taskTrackerb6.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class NotificationResponse {

    private Long id;
    private Long notifierId;
    private String fullName;
    private String photo;
    private LocalDateTime createdAt;
    private NotificationType type;
    private String message;

    public NotificationResponse(Notification notification) {
        this.id = notification.getId();
        this.notifierId = notification.getFromUser().getId();
        this.fullName = notification.getFromUser().getFirstName() + " " + notification.getFromUser().getLastName();
        this.photo = notification.getFromUser().getPhotoLink();
        this.createdAt = notification.getCreatedAt();
        this.type = notification.getNotificationType();
        this.message = notification.getMessage();
    }

}
