package kg.peaksoft.taskTrackerb6.db.service;

import kg.peaksoft.taskTrackerb6.db.model.Attachment;
import kg.peaksoft.taskTrackerb6.db.model.Card;
import kg.peaksoft.taskTrackerb6.db.repository.AttachmentRepository;
import kg.peaksoft.taskTrackerb6.db.repository.CardRepository;
import kg.peaksoft.taskTrackerb6.dto.request.AttachmentRequest;
import kg.peaksoft.taskTrackerb6.dto.response.AttachmentResponse;
import kg.peaksoft.taskTrackerb6.dto.response.SimpleResponse;
import kg.peaksoft.taskTrackerb6.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final CardRepository cardRepository;

    public AttachmentResponse addAttachmentToCard(Long cardId, AttachmentRequest request) {
        Card card = cardRepository.findById(cardId).orElseThrow(
                () -> {
                    log.error("Card with id: {} not found!", cardId);
                    throw new NotFoundException("Card with id: " + cardId + " not found!");
                }
        );

        Attachment attachment = new Attachment(request);
        card.addAttachment(attachment);
        attachment.setCard(card);
        Attachment save = attachmentRepository.save(attachment);
        log.info("Attachment successfully added to card!");
        return new AttachmentResponse(save);
    }

    public SimpleResponse deleteAttachment(Long id) {
        Attachment attachment = attachmentRepository.findById(id).orElseThrow(
                () -> {
                    log.error("Attachment with id: {} not found!", id);
                    throw new NotFoundException("Attachment with id: " + id + " not found!");
                }
        );

        attachmentRepository.deleteAttachment(attachment.getId());
        log.info("Attachment with id: {} successfully deleted!", id);
        return new SimpleResponse("Attachment with id: " + id + " successfully deleted!", "DELETE");
    }
}
