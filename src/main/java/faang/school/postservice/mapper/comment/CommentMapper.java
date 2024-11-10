package faang.school.postservice.mapper.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.ResponseCommentDto;
import faang.school.postservice.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {

    @Mapping(source = "post.id", target = "postId")
    CommentDto toCommentDto(Comment comment);

    @Mapping(source = "post.id", target = "postId")
    ResponseCommentDto toResponseDto(Comment comment);

    Comment toEntity(CommentDto commentDto);

    List<ResponseCommentDto> toListResponseDto(List<Comment> comments);
}
