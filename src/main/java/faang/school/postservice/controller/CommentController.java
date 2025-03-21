package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.service.CommentService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/comment")
@RequiredArgsConstructor
public class CommentController {

    CommentService commentService;
    CommentMapper commentMapper;
    UserContext userContext;


    @GetMapping("s/{postId}")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getComments(@PathVariable Long postId) {

        return List.of(
                CommentDto.builder().content("1").build(),
                CommentDto.builder().content("2").build());
    }

    @GetMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto getComment(@PathVariable Long commentId) {

        return CommentDto.builder().content("3").build();
    }

    @PostMapping("/{postId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable Long postId, @NotNull @RequestBody CommentDto commentDto) {

        return null;
    }

    @PutMapping("/{commentId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public CommentDto updateComment(@PathVariable Long commentId, @NotNull @RequestBody CommentDto commentDto) {

        return null;
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public boolean deleteComment(@PathVariable Long commentId) {

        return false;
    }
}
