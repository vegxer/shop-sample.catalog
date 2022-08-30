package ru.vegxer.shopsample.catalog.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import ru.vegxer.shopsample.catalog.dto.request.CategoryPostRequest;
import ru.vegxer.shopsample.catalog.dto.response.CategoryResponse;
import ru.vegxer.shopsample.catalog.dto.response.CategoryShortResponse;
import ru.vegxer.shopsample.catalog.entity.Category;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mappings({
        @Mapping(target = "parent", ignore = true),
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "children", ignore = true),
        @Mapping(target = "attachment", ignore = true),
    })
    Category mapToEntity(CategoryPostRequest category);

    @Mappings({
        @Mapping(target = "hasChildren", source = "children", qualifiedByName = "hasChildren"),
        @Mapping(target = "imageThumbnailPath", source = "attachment.thumbnailPath"),
        @Mapping(target = "imagePath", source = "attachment.path")
    })
    CategoryResponse mapToResponse(Category category);

    CategoryShortResponse mapToShortResponse(Category category);

    @Named("hasChildren")
    default boolean hasChildren(List<Category> categories) {
        return categories != null && categories.size() > 0;
    }
}
