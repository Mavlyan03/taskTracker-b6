package kg.peaksoft.taskTrackerb6.dto.response;

import kg.peaksoft.taskTrackerb6.db.model.Attachment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class AttachmentResponse {

    private Long id;
    private String documentLink;
    private LocalDateTime attachedDate;

    public AttachmentResponse(Attachment attachment) {
        this.id = attachment.getId();
        this.documentLink = attachment.getDocumentLink();
        this.attachedDate = attachment.getAttachedDate();
    }

}
