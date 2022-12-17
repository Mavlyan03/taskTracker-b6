package kg.peaksoft.taskTrackerb6.db.service;

import kg.peaksoft.taskTrackerb6.db.model.Card;
import kg.peaksoft.taskTrackerb6.db.model.Estimation;
import kg.peaksoft.taskTrackerb6.db.repository.CardRepository;
import kg.peaksoft.taskTrackerb6.db.repository.EstimationRepository;
import kg.peaksoft.taskTrackerb6.dto.request.EstimationRequest;
import kg.peaksoft.taskTrackerb6.dto.response.EstimationResponse;
import kg.peaksoft.taskTrackerb6.enums.ReminderType;
import kg.peaksoft.taskTrackerb6.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
@Transactional
@RequiredArgsConstructor
public class EstimationService {

    private final EstimationRepository estimationRepository;
    private final CardRepository cardRepository;

    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    private final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private LocalDate parse(String value) {
        try {
            return DATE_FORMATTER.parse(value, LocalDate::from);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private LocalDateTime parseToLocalDateTime(String value) {
        try {
            return DATE_TIME_FORMATTER.parse(value, LocalDateTime::from);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public EstimationResponse addEstimationToCard(Long cardId, EstimationRequest request) {
        Card card = cardRepository.findById(cardId).orElseThrow(
                () -> new NotFoundException("Card with id: " + cardId + " not found!")
        );

        LocalDate startDate = parse(request.getStartDate());
        LocalDate dueDate = parse(request.getDueDate());
        String parsedStartDate = startDate.toString() + " " + request.getStartTime();
        String parsedDueDate = dueDate.toString() + " " + request.getDueTime();
        LocalDateTime startTime = parseToLocalDateTime(parsedStartDate);
        LocalDateTime dueTime = parseToLocalDateTime(parsedDueDate);
        Estimation estimation = new Estimation(startDate, dueDate, startTime, dueTime);
        if (request.getReminder().equals("5")) {
            estimation.setReminder(ReminderType.FIVE_MINUTE);
            LocalDateTime notification = dueTime.minusMinutes(5);
            estimation.setNotificationTime(notification);
        }

        if (request.getReminder().equals("30")) {
            estimation.setReminder(ReminderType.THIRTY_MINUTE);
            LocalDateTime notification = dueTime.minusMinutes(30);
            estimation.setNotificationTime(notification);
        }

        if (request.getReminder().equals("60")) {
            estimation.setReminder(ReminderType.ONE_HOUR);
            LocalDateTime notification = dueTime.minusMinutes(60);
            estimation.setNotificationTime(notification);
        }

        if (request.getReminder().equals("None")) {
            estimation.setReminder(ReminderType.NONE);
        }

        card.setEstimation(estimation);
        estimation.setCard(card);
        Estimation save = estimationRepository.save(estimation);
        return new EstimationResponse(
                save.getId(),
                save.getStartDate(),
                save.getDueDate(),
                save.getStartTime(),
                save.getDueTime(),
                save.getReminder()
        );
    }

}
