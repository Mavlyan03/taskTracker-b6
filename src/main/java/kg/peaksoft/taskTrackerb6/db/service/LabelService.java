package kg.peaksoft.taskTrackerb6.db.service;

import kg.peaksoft.taskTrackerb6.db.model.Card;
import kg.peaksoft.taskTrackerb6.db.model.Label;
import kg.peaksoft.taskTrackerb6.db.repository.CardRepository;
import kg.peaksoft.taskTrackerb6.db.repository.LabelRepository;
import kg.peaksoft.taskTrackerb6.dto.request.LabelRequest;
import kg.peaksoft.taskTrackerb6.dto.request.UpdateRequest;
import kg.peaksoft.taskTrackerb6.dto.response.LabelResponse;
import kg.peaksoft.taskTrackerb6.dto.response.SimpleResponse;
import kg.peaksoft.taskTrackerb6.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class LabelService {

    private final LabelRepository labelRepository;
    private final CardRepository cardRepository;


    public SimpleResponse willDeletedLabels(Long cardId, List<Long> labelsIds) {
        Card card = cardRepository.findById(cardId).orElseThrow(
                () -> new NotFoundException("Card with id: " + cardId + " not found!")
        );

        List<Label> labels = card.getLabels();
        if (labels != null) {
            for (Long id : labelsIds) {
                for (Label label : labels) {
                    if (label.getId().equals(id)) {
                        labelRepository.deleteLabel(label.getId());
                    }
                }
            }
        }

        return new SimpleResponse("Labels is deleted!", "DELETE");
    }


    public LabelResponse updateLabel(UpdateRequest update) {
        Label label = labelRepository.findById(update.getId()).orElseThrow(
                () -> {
                    log.error("Label with id: {} not found!", update.getId());
                    throw new NotFoundException(String.format("Label with id %s not found", update.getId()));
                }
        );

        label.setDescription(update.getNewTitle());
        labelRepository.save(label);
        log.info("Label successfully created");
        return labelRepository.getLabelResponse(label.getId());
    }

    public LabelResponse getLabelById(Long id) {
        Label label = labelRepository.findById(id).orElseThrow(
                () -> {
                    log.error("Label with id: {} not found!", id);
                    throw new NotFoundException("Label with id: " + id + " not found!");
                }
        );

        return labelRepository.getLabelResponse(label.getId());
    }

    public List<LabelResponse> getAllLabelsByCardId(Long cardId) {
        log.info("Get all label by card's id");
        return labelRepository.getAllLabelResponses(cardId);
    }
}