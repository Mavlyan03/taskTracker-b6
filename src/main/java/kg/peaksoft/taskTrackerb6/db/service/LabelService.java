package kg.peaksoft.taskTrackerb6.db.service;

import kg.peaksoft.taskTrackerb6.db.model.Card;
import kg.peaksoft.taskTrackerb6.db.model.Label;
import kg.peaksoft.taskTrackerb6.db.repository.CardRepository;
import kg.peaksoft.taskTrackerb6.db.repository.LabelRepository;
import kg.peaksoft.taskTrackerb6.dto.request.AddLabelRequest;
import kg.peaksoft.taskTrackerb6.dto.request.LabelRequest;
import kg.peaksoft.taskTrackerb6.dto.request.UpdateLabelRequest;
import kg.peaksoft.taskTrackerb6.dto.response.LabelResponse;
import kg.peaksoft.taskTrackerb6.dto.response.SimpleResponse;
import kg.peaksoft.taskTrackerb6.exceptions.BadRequestException;
import kg.peaksoft.taskTrackerb6.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class LabelService {

    private final LabelRepository labelRepository;
    private final CardRepository cardRepository;

    public LabelResponse saveLabel(LabelRequest labelRequest) {
        Label label = new Label(labelRequest.getDescription(), labelRequest.getColor());
        Label save = labelRepository.save(label);
        return labelRepository.getLabelResponse(save.getId());
    }

    public SimpleResponse deleteLabelFromCard(Long cardId, Long labelId) {
        Card card = cardRepository.findById(cardId).orElseThrow(
                () -> new NotFoundException("Card with id: " + cardId + " not found!")
        );

        Label label = labelRepository.findById(labelId).orElseThrow(
                () -> new NotFoundException("Label with id: " + labelId + " not found!")
        );

        if (card.getLabels().contains(label)) {
            card.getLabels().remove(label);
            log.info("label is removed from card!");
        }

        return new SimpleResponse("Label successfully deleted!", "DELETE");
    }

    public LabelResponse updateLabel(UpdateLabelRequest update) {
        Label label = labelRepository.findById(update.getId()).orElseThrow(
                () -> {
                    log.error("Label with id: {} not found!", update.getId());
                    throw new NotFoundException(String.format("Label with id %s not found!", update.getId()));
                }
        );

        label.setColor(update.getColor());
        label.setDescription(update.getNewTitle());
        labelRepository.save(label);
        log.info("Label successfully updated!");
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
        Card card = cardRepository.findById(cardId).orElseThrow(
                () -> new NotFoundException("Card with id: " + cardId + " not found!")
        );


        List<Label> cardLabels = card.getLabels();
        List<LabelResponse> labelResponses = new ArrayList<>();
        for (Label l : cardLabels) {
            labelResponses.add(labelRepository.getLabelResponse(l.getId()));
        }

        log.info("Get all labels by card's id");
        return labelResponses;
    }

    public SimpleResponse addLabelToCard(AddLabelRequest request) {
        Card card = cardRepository.findById(request.getCardId()).orElseThrow(
                () -> new NotFoundException("Card with id: " + request.getCardId() + " not found!")
        );

        Label label = labelRepository.findById(request.getLabelId()).orElseThrow(
                () -> new NotFoundException("Label with id: " + request.getLabelId() + " not found!")
        );

        if (!card.getLabels().contains(label)) {
            card.addLabel(label);
            label.addCard(card);
            return new SimpleResponse("Label added to this card!", "OK");
        } else {
            throw new BadRequestException("Label already added to card!");
        }
    }

    public SimpleResponse deleteLabelById(Long id) {
        Label label = labelRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Label with id: " + id + " not found!")
        );

        label.setCards(null);
        labelRepository.deleteLabel(id);
        return new SimpleResponse("Label successfully deleted!", "OK");
    }

    public List<LabelResponse> getAllReadyLabels() {
        List<Label> getAll = labelRepository.findAll();
        List<LabelResponse> labelResponses = new ArrayList<>();
        for (Label label : getAll) {
            labelResponses.add(labelRepository.getLabelResponse(label.getId()));
        }

        return labelResponses;
    }
}
