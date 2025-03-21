package faang.school.postservice.service;

import faang.school.postservice.model.Comment;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    public Comment getComment(Long commentId) {
        return new Comment();
    }

    public Comment getAllComments(Long postId) {
        return new Comment();
    }

    public Comment createComment(Comment comment) {
        return new Comment();
    }

    public Comment updateComment(Long commentId, String message) {
        return new Comment();
    }

    public boolean deleteComment(Long commentId) {
        return true;
    }

}
