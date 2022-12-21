package kg.peaksoft.taskTrackerb6.db.model;

import kg.peaksoft.taskTrackerb6.dto.request.AttachmentRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.CascadeType.*;

@Entity
@Table(name = "attachments")
@Getter
@Setter
@NoArgsConstructor
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attachment_gen")
    @SequenceGenerator(name = "attachment_gen", sequenceName = "attachment_seq", allocationSize = 1, initialValue = 2)
    private Long id;

    private String documentLink;

    private LocalDateTime attachedDate;

    @ManyToOne(cascade = {DETACH, REFRESH, MERGE})
    private Card card;

    public Attachment(AttachmentRequest request) {
        this.documentLink = request.getDocumentLink();
    }
}
