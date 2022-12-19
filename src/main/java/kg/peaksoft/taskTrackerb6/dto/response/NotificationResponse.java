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
    private String boardTitle;
    private String background;
    private Long fromUserId;
    private String firstName;
    private String lastName;
    private String image;
    private LocalDateTime createdAt;
    private NotificationType type;
    private Boolean isRead;

    public NotificationResponse(Notification notification) {
        this.id = notification.getId();
        this.boardTitle = notification.getBoard().getTitle();
        this.background = notification.getBoard().getBackground();
        this.fromUserId = notification.getFromUser().getId();
        this.firstName = notification.getFromUser().getFirstName();
        this.lastName = notification.getFromUser().getLastName();
        this.image = notification.getFromUser().getImage();
        this.createdAt = notification.getCreatedAt();
        this.type = notification.getNotificationType();
        this.isRead = notification.getIsRead();
    }

}
