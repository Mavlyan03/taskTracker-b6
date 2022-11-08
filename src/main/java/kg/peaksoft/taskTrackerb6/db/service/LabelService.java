package kg.peaksoft.taskTrackerb6.db.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import kg.peaksoft.taskTrackerb6.db.model.Label;
import kg.peaksoft.taskTrackerb6.db.repository.LabelRepository;
import kg.peaksoft.taskTrackerb6.dto.request.LabelUpdateRequest;
import kg.peaksoft.taskTrackerb6.dto.response.SimpleResponse;
import kg.peaksoft.taskTrackerb6.exceptions.NotFoundException;

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

}
