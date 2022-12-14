package kg.peaksoft.taskTrackerb6.db.service;

import kg.peaksoft.taskTrackerb6.db.model.Card;
import kg.peaksoft.taskTrackerb6.db.model.Label;
import kg.peaksoft.taskTrackerb6.db.repository.CardRepository;
import kg.peaksoft.taskTrackerb6.db.repository.LabelRepository;
import kg.peaksoft.taskTrackerb6.dto.request.LabelRequest;
import kg.peaksoft.taskTrackerb6.dto.request.UpdateRequest;
import kg.peaksoft.taskTrackerb6.dto.response.LabelResponse;
import kg.peaksoft.taskTrackerb6.dto.response.SimpleResponse;
import kg.peaksoft.taskTrackerb6.exceptions.BadRequestException;
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

    public SimpleResponse saveLabel(LabelRequest labelRequest) {
        Label label = new Label(labelRequest.getDescription(), labelRequest.getColor());
        labelRepository.save(label);
        return new SimpleResponse("New label saved!", "OK");
    }

    public SimpleResponse deleteLabelFromCard(Long cardId, Long labelId) {
        Card card = cardRepository.findById(cardId).orElseThrow(
                () -> new NotFoundException("Card with id: " + cardId + " not found!")
        );

        Label label = labelRepository.findById(labelId).orElseThrow(
                () -> new NotFoundException("Label with id: " + labelId + " not found!")
        );

        if (!card.getLabels().contains(label)) {
            card.getLabels().remove(label);
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
        label.setColor(update.getColor());
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

    public SimpleResponse addLabelToCard(AddLabelRequest request) {
        Card card = cardRepository.findById(request.getLabelId()).orElseThrow(
                () -> new NotFoundException("Card with id: " + request.getCardId() + " not found!")
        );

        Label label = labelRepository.findById(request.getLabelId()).orElseThrow(
                () -> new NotFoundException("Label with id: " + request.getLabelId() + " not found!")
        );
        if (!card.getLabels().contains(label)) {
            card.addLabel(label);
            return new SimpleResponse("Label added to this card", "OK");
        } else {
            throw new BadRequestException("Label already added to card");
        }
    }

    public SimpleResponse deleteLabelById(Long id) {
        labelRepository.deleteById(id);
        return new SimpleResponse("Card deleted", "OK");
    }
}