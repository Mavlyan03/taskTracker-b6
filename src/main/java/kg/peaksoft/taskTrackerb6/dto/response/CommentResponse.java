package kg.peaksoft.taskTrackerb6.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentResponse {

    private Long id;
    private String text;
    private String createdAt;
    private CommentedUserResponse commentedUserResponse;

    public CommentResponse(Long id, String text, String createdAt, CommentedUserResponse commentedUserResponse) {
        this.id = id;
        this.text = text;
        this.createdAt = createdAt;
        this.commentedUserResponse = commentedUserResponse;
    }
}
