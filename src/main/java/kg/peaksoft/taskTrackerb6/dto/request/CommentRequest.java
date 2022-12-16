package kg.peaksoft.taskTrackerb6.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentRequest {

    private String text;
    private String createdAt;
}
