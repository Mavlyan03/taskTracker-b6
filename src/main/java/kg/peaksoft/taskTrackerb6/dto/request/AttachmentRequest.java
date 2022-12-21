package kg.peaksoft.taskTrackerb6.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class AttachmentRequest {

    private String documentLink;
    private LocalDateTime attachedDate;
}
