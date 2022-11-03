package kg.peaksoft.taskTrackerb6.db.service;

import kg.peaksoft.taskTrackerb6.db.model.Card;
import kg.peaksoft.taskTrackerb6.db.model.Comment;
import kg.peaksoft.taskTrackerb6.db.model.User;
import kg.peaksoft.taskTrackerb6.db.repository.CardRepository;
import kg.peaksoft.taskTrackerb6.db.repository.CommentRepository;
import kg.peaksoft.taskTrackerb6.db.repository.UserRepository;
import kg.peaksoft.taskTrackerb6.dto.request.CommentRequest;
import kg.peaksoft.taskTrackerb6.dto.response.CommentResponse;
import kg.peaksoft.taskTrackerb6.dto.response.SimpleResponse;
import kg.peaksoft.taskTrackerb6.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CardRepository cardRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public CommentResponse saveComment(Long cardId, CommentRequest request){
        Card card = cardRepository.findById(cardId).orElseThrow(
                ()-> new NotFoundException("Card with id: "+cardId+" not found")
        );
        Comment comment = new Comment();
        comment.setUser(getUser());
        comment.setCard(card);
        comment.setText(request.getText());
        comment.setCreatedDate(LocalDateTime.now());
        commentRepository.save(comment);
        return new CommentResponse (comment.getUser().getPhotoLink(),
                                    comment.getUser().getFirstName(),
                                    comment.getText(), comment.getCreatedDate());
    }

    public CommentResponse editComment(Long id, CommentRequest request){
        Comment comment = commentRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Comment with id "+id+" not found")
        );
        comment.setText(request.getText());
        comment.setCreatedDate(LocalDateTime.now());
        Comment comment1 = commentRepository.save(comment);
        return new CommentResponse (comment1.getUser().getPhotoLink(),
                                    comment1.getUser().getFirstName(),
                                    comment1.getText(), comment1.getCreatedDate());
    }

    public SimpleResponse deleteComment(Long id){
        Comment comment = commentRepository.findById(id).orElseThrow(
                ()-> new NotFoundException("Comment with id: "+id+" not found")
        );
        commentRepository.delete(comment);
        return new SimpleResponse("Comment with id: "+id+" successfully deleted", "DELETED");
    }

    public List<CommentResponse> findAllComments(Long id){
        List<Comment> comments = commentRepository.findAllComments(id);
        List<CommentResponse> commentResponses = new ArrayList<>();
        for (Comment comment : comments) {
            commentResponses.add(new CommentResponse(comment.getUser().getPhotoLink(),
                                                     comment.getUser().getFirstName(),
                                                     comment.getText(), comment.getCreatedDate()));
        }
        return commentResponses;
    }

    private User getUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();
        return userRepository.findByEmail(login).orElseThrow(
                ()-> new NotFoundException("User not found")
        );
    }
}
