package kg.peaksoft.taskTrackerb6.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class CommentResponse {

    private Long id;
    private String text;
    private String createdAt;
    private CommentedUserResponse commentedUserResponse;
    private Boolean isMyComment;

    public CommentResponse(String text, String createdAt, CommentedUserResponse commentedUserResponse, Boolean isMyComment) {
        this.text = text;
        this.createdAt = createdAt;
        this.commentedUserResponse = commentedUserResponse;
        this.isMyComment = isMyComment;
    }

    public CommentResponse(Long id, String text, String createdAt, CommentedUserResponse response) {
        this.id = id;
        this.text = text;
        this.createdAt = createdAt;
        this.commentedUserResponse = response;
    }
}
