package kg.peaksoft.taskTrackerb6.db.service;

import kg.peaksoft.taskTrackerb6.db.model.Label;
import kg.peaksoft.taskTrackerb6.db.repository.LabelRepository;
import kg.peaksoft.taskTrackerb6.dto.request.LabelUpdateRequest;
import kg.peaksoft.taskTrackerb6.dto.response.LabelResponse;
import kg.peaksoft.taskTrackerb6.dto.response.SimpleResponse;
import kg.peaksoft.taskTrackerb6.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class LabelService {

    private final LabelRepository labelRepository;

    public SimpleResponse updateLabel(Long id, LabelUpdateRequest labelUpdateRequest) {
        Label label = labelRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format("Label with id %s not found", id))
        );

        label.setDescription(labelUpdateRequest.getDescription());
        labelRepository.save(label);
        
        return new SimpleResponse("Updated", "OK");
    }

    public LabelResponse getLabelById(Long id) {
        Label label = labelRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Label with id: " + id + " not found!")
        );

        return new LabelResponse(
                label.getId(),
                label.getDescription(),
                label.getColor()
        );
    }

    public List<LabelResponse> getAllLabelsByCardId(Long cardId) {
        return labelRepository.getAllLabelResponses(cardId);
    }
}
