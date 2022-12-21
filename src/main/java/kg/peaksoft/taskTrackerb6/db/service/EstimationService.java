package kg.peaksoft.taskTrackerb6.db.service;

import kg.peaksoft.taskTrackerb6.db.model.Card;
import kg.peaksoft.taskTrackerb6.db.model.Estimation;
import kg.peaksoft.taskTrackerb6.db.model.User;
import kg.peaksoft.taskTrackerb6.db.repository.CardRepository;
import kg.peaksoft.taskTrackerb6.db.repository.EstimationRepository;
import kg.peaksoft.taskTrackerb6.db.repository.UserRepository;
import kg.peaksoft.taskTrackerb6.dto.request.EstimationRequest;
import kg.peaksoft.taskTrackerb6.dto.response.EstimationResponse;
import kg.peaksoft.taskTrackerb6.enums.ReminderType;
import kg.peaksoft.taskTrackerb6.exceptions.BadCredentialException;
import kg.peaksoft.taskTrackerb6.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class EstimationService {

    private final EstimationRepository estimationRepository;
    private final CardRepository cardRepository;
    private final UserRepository userRepository;

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

    public EstimationResponse addEstimationToCard(Long cardId, EstimationRequest request) {
        User user = getAuthenticateUser();
        Card card = cardRepository.findById(cardId).orElseThrow(
                () -> new NotFoundException("Card with id: " + cardId + " not found!")
        );

        Estimation e = estimationRepository.findEstimationByCardId(card.getId());
        if (e != null) {
            throw new BadCredentialException("Card with id: " + card.getId() + " already have estimation!");
        }

        LocalDate startDate = parse(request.getStartDate());
        LocalDate dueDate = parse(request.getDueDate());
        String parsedStartDate = startDate.toString() + " " + request.getStartTime();
        String parsedDueDate = dueDate.toString() + " " + request.getDueTime();
        LocalDateTime startTime = parseToLocalDateTime(parsedStartDate);
        LocalDateTime dueTime = parseToLocalDateTime(parsedDueDate);
        Estimation estimation = new Estimation(startDate, dueDate, startTime, dueTime);
        estimation.setUser(user);
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

        estimation.setCard(card);
        Estimation save = estimationRepository.save(estimation);
        return new EstimationResponse(
                save.getId(),
                save.getStartTime(),
                save.getDueTime(),
                save.getReminder()
        );
    }

    public EstimationResponse updateEstimation(Long estimationId, EstimationRequest request) {
        Estimation estimation = estimationRepository.findById(estimationId).orElseThrow(
                () -> new NotFoundException("Estimation with id: " + estimationId + " not found!")
        );

        LocalDate startDate = parse(request.getStartDate());
        LocalDate dueDate = parse(request.getDueDate());
        String parsedStartDate = startDate.toString() + " " + request.getStartTime();
        String parsedDueDate = dueDate.toString() + " " + request.getDueTime();
        LocalDateTime startTime = parseToLocalDateTime(parsedStartDate);
        LocalDateTime dueTime = parseToLocalDateTime(parsedDueDate);
        if (request.getStartDate() != null || !request.getStartDate().isEmpty()) {
            estimation.setStartDate(parse(request.getStartDate()));
        } else {
            estimation.setStartDate(estimation.getStartDate());
        }

        if (request.getDueDate() != null || !request.getDueDate().isEmpty()) {
            estimation.setDueDate(parse(request.getDueDate()));
        } else {
            estimation.setDueDate(estimation.getDueDate());
        }

        if (request.getStartTime() != null || !request.getStartTime().isEmpty()) {
            estimation.setStartTime(startTime);
        } else {
            estimation.setStartTime(estimation.getStartTime());
        }

        if (request.getDueTime() != null || !request.getDueTime().isEmpty()) {
            estimation.setDueTime(dueTime);
        } else {
            estimation.setDueTime(estimation.getDueTime());
        }

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

        Estimation save = estimationRepository.save(estimation);
        return new EstimationResponse(
                save.getId(),
                save.getStartTime(),
                save.getDueTime(),
                save.getReminder()
        );
    }

}
