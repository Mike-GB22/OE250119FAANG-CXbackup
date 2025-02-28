package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.like.LikeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {
    @Mock
    private PostRepository postRepository;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserContext userContext;
    @Mock
    private UserServiceClient userServiceClient;
    @InjectMocks
    private LikeService likeService;

    Like like;
    Like like1;
    Comment comment;
    Post post;

    @BeforeEach
    void setUp() {
        like1 = new Like();
        like1.setUserId(2L);
        like1.setId(3);

        post = new Post();
        post.setId(1L);
        post.setLikes(List.of(like1));

        comment = new Comment();
        comment.setId(4L);
        comment.setLikes(List.of(like1));
        comment.setPost(post);

        like = new Like();
        like.setPost(post);
        like.setComment(comment);
    }


    @Test
    public void testLikePostLikeIsSavedToDataBase() {
        Mockito.when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        Mockito.when(userContext.getUserId()).thenReturn(1L);
        Mockito.when(userServiceClient.getUser(1L)).thenReturn(new UserDto(1L, "testUser", "t@mail.com"));
        likeService.likePost(like);

        Mockito.verify(likeRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    public void testDislikePostLikeIsRemovedFromDataBase() {
        Mockito.when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        Mockito.when(userContext.getUserId()).thenReturn(2L);
        Mockito.when(userServiceClient.getUser(2L)).thenReturn(new UserDto(2L, "testUser", "t@mail.com"));
        likeService.dislikePost(like);

        Mockito.verify(likeRepository, Mockito.times(1)).deleteById(like.getId());
    }

    @Test
    public void testLikeCommentLikeIsSavedToDataBase() {
        Mockito.when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        Mockito.when(userContext.getUserId()).thenReturn(1L);
        Mockito.when(userServiceClient.getUser(1L)).thenReturn(new UserDto(1L, "testUser", "t@mail.com"));
        likeService.likeComment(like);

        Mockito.verify(likeRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    public void testDislikeCommentLikeIsRemovedFromDataBase() {
        Mockito.when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        Mockito.when(userContext.getUserId()).thenReturn(2L);
        Mockito.when(userServiceClient.getUser(2L)).thenReturn(new UserDto(2L, "testUser", "t@mail.com"));
        likeService.dislikeComment(like);

        Mockito.verify(likeRepository, Mockito.times(1)).deleteById(like.getId());
    }
}
