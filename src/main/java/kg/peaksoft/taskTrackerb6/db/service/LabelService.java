package kg.peaksoft.taskTrackerb6.db.service;

import kg.peaksoft.taskTrackerb6.db.model.Label;
import kg.peaksoft.taskTrackerb6.db.repository.LabelRepository;
import kg.peaksoft.taskTrackerb6.dto.request.LabelRequest;
import kg.peaksoft.taskTrackerb6.dto.response.LabelResponse;
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

    public LabelResponse updateLabel(Long id, LabelRequest labelRequest) {
        Label label = labelRepository.findById(id).orElseThrow(
                () -> {
                    log.error("Label with id: {} not found!", id);
                    throw new NotFoundException(String.format("Label with id %s not found", id));
                }
        );

        label.setDescription(labelRequest.getDescription());
        labelRepository.save(label);
        log.info("Label successfully created");
        return mapToResponse(label);
    }

    public LabelResponse getLabelById(Long id) {
        Label label = labelRepository.findById(id).orElseThrow(
                () -> {
                    log.error("Label with id: {} not found!", id);
                    throw new NotFoundException("Label with id: " + id + " not found!");
                }
        );

        return mapToResponse(label);
    }

    public List<LabelResponse> getAllLabelsByCardId(Long cardId) {
        log.info("Get all label by card id");
        return labelRepository.getAllLabelResponses(cardId);
    }

    private LabelResponse mapToResponse(Label label) {
        return new LabelResponse(label.getId(), label.getDescription(), label.getColor());
    }
}