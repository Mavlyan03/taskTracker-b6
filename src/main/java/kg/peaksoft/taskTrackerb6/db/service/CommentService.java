package kg.peaksoft.taskTrackerb6.db.service;

import kg.peaksoft.taskTrackerb6.db.model.Card;
import kg.peaksoft.taskTrackerb6.db.model.Comment;
import kg.peaksoft.taskTrackerb6.db.model.User;
import kg.peaksoft.taskTrackerb6.db.repository.CardRepository;
import kg.peaksoft.taskTrackerb6.db.repository.CommentRepository;
import kg.peaksoft.taskTrackerb6.db.repository.UserRepository;
import kg.peaksoft.taskTrackerb6.dto.request.CommentRequest;
import kg.peaksoft.taskTrackerb6.dto.request.UpdateRequest;
import kg.peaksoft.taskTrackerb6.dto.response.CommentResponse;
import kg.peaksoft.taskTrackerb6.dto.response.SimpleResponse;
import kg.peaksoft.taskTrackerb6.exceptions.BadCredentialException;
import kg.peaksoft.taskTrackerb6.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CardRepository cardRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public CommentResponse saveComment(Long cardId, CommentRequest request) {
        Card card = cardRepository.findById(cardId).orElseThrow(
                () -> {
                    log.error("Card with id: {} not found!", cardId);
                    throw new NotFoundException("Card with id: " + cardId + " not found!");
                }
        );

        User user = getAuthenticatedUser();
        Comment comment = new Comment(request.getText(), request.getCreatedAt(), user);
        comment.setCard(card);
        commentRepository.save(comment);
        log.info("Comment successfully created!");
        return new CommentResponse(
                comment.getId(),
                comment.getText(),
                comment.getCreatedAt(),
                userRepository.getCommentedUserResponse(comment.getUser().getId())
        );
    }

    public CommentResponse editComment(UpdateRequest request) {
        Comment comment = commentRepository.findById(request.getId()).orElseThrow(
                () -> {
                    log.error("Comment with id: {} not found!", request.getId());
                    throw new NotFoundException("Comment with id " + request.getId() + " not found!");
                }
        );

        if (!getAuthenticatedUser().equals(comment.getUser())) {
            log.error("You can not edit this comment!");
            throw new BadCredentialException("You cannot edit this comment!");
        }

        comment.setText(request.getNewTitle());
        comment.setCreatedAt(comment.getCreatedAt());
        Comment comment1 = commentRepository.save(comment);
        log.info("Comment with id: {} successfully edited", request.getId());
        return new CommentResponse(
                comment1.getId(),
                comment1.getText(),
                comment1.getCreatedAt(),
                userRepository.getCommentedUserResponse(comment1.getUser().getId())
        );
    }

    public SimpleResponse deleteComment(Long id) {
        Comment comment = commentRepository.findById(id).orElseThrow(
                () -> {
                    log.error("Comment with id: {} not found!", id);
                    throw new NotFoundException("Comment with id: " + id + " not found!");
                }
        );

        if (!getAuthenticatedUser().equals(comment.getUser())) {
            log.error("You can not delete this comment!");
            throw new BadCredentialException("You can not delete this comment!");
        }

        commentRepository.delete(comment);
        log.info("Comment with id: {} successfully deleted!", id);
        return new SimpleResponse("Comment with id: " + id + " successfully deleted", "DELETE");
    }

    public List<CommentResponse> findAllComments(Long id) {
        User user = getAuthenticatedUser();
        List<Comment> comments = commentRepository.getAllSortedById(id);
        List<CommentResponse> commentResponses = new ArrayList<>();
        for (Comment comment : comments) {
            if (comment.getUser().equals(user)) {
                commentResponses.add(new CommentResponse(
                                comment.getId(),
                                comment.getText(),
                                comment.getCreatedAt(),
                                userRepository.getCommentedUserResponse(comment.getUser().getId()),
                                true
                        )
                );
            } else {
                commentResponses.add(new CommentResponse(
                                comment.getId(),
                                comment.getText(),
                                comment.getCreatedAt(),
                                userRepository.getCommentedUserResponse(comment.getUser().getId()),
                                false
                        )
                );
            }
        }

        log.info("Get all comments by card's id");
        return commentResponses;
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String login = authentication.getName();
        return userRepository.findUserByEmail(login).orElseThrow(
                () -> {
                    log.error("User not found!");
                    throw new NotFoundException("User not found!");
                }
        );
    }
}
