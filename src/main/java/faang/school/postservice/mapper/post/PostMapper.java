package faang.school.postservice.mapper.post;

import faang.school.postservice.dto.post.PostDraftCreateDto;
import faang.school.postservice.dto.post.PostDraftResponseDto;
import faang.school.postservice.model.Album;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface PostMapper {

    Post toEntityFromDraftDto(PostDraftCreateDto dto);

    @Mapping(source = "albums", target = "albumsIds", qualifiedByName = "mapAlbumsToIds")
    @Mapping(source = "resources", target = "resourcesIds", qualifiedByName = "mapResourcesToIds")
    PostDraftResponseDto toDraftDtoFromPost(Post post);

    @Named("mapAlbumsToIds")
    default List<Long> mapAlbumsToIds(List<Album> albums) {
        return albums.stream().map(Album::getId).toList();
    }

    @Named("mapResourcesToIds")
    default List<Long> mapResourcesToIds(List<Resource> resources) {
        return resources.stream().map(Resource::getId).toList();
    }
}
