package kg.peaksoft.taskTrackerb6.config.reminder;

import kg.peaksoft.taskTrackerb6.db.model.Estimation;
import kg.peaksoft.taskTrackerb6.db.model.Notification;
import kg.peaksoft.taskTrackerb6.db.repository.EstimationRepository;
import kg.peaksoft.taskTrackerb6.db.repository.NotificationRepository;
import kg.peaksoft.taskTrackerb6.enums.NotificationType;
import kg.peaksoft.taskTrackerb6.enums.ReminderType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Configuration
@EnableScheduling
@Slf4j
@RequiredArgsConstructor
public class ScheduledConfig {

    private final EstimationRepository estimationRepository;
    private final NotificationRepository notificationRepository;
    private final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");


    private LocalDateTime parseToLocalDateTime(String value) {
        try {
            return DATE_TIME_FORMATTER.parse(value, LocalDateTime::from);
        } catch (DateTimeParseException e) {
            return null;
        }
    }


    @Scheduled(cron = "0 0/1 * * * *")
    public void reminder() {
        List<Estimation> estimations = estimationRepository.findAll();
        for (Estimation e : estimations) {
            if (!e.getReminder().equals(ReminderType.NONE)) {
                LocalDateTime nowForParse = LocalDateTime.now();
                LocalDate today = LocalDate.now();
                LocalTime timeNow = nowForParse.toLocalTime();
                String[] parseTime = timeNow.toString().split(":");
                String parsed = today + " " + parseTime[0] + ":" + parseTime[1];
                LocalDateTime now = parseToLocalDateTime(parsed);
                assert now != null;
                if (now.equals(e.getNotificationTime())) {
                    Notification notification = new Notification();
                    notification.setCard(e.getCard());
                    notification.setNotificationType(NotificationType.REMINDER);
                    notification.setFromUser(e.getCard().getColumn().getCreator());
                    notification.setBoard(e.getCard().getColumn().getBoard());
                    notification.setColumn(e.getCard().getColumn());
                    notification.setEstimation(e);
                    notification.setUser(e.getCard().getCreator());
                    notification.setBoard(e.getCard().getColumn().getBoard());
                    notification.setCreatedAt(LocalDateTime.now());
                    notification.setIsRead(false);
                    notification.setMessage("The deadline given to the card: " + e.getCard() + " task will end in: " + e.getReminder() + " minutes");
                    notificationRepository.save(notification);
                    log.info("notification is saved");
                }
            }
        }
    }
}
