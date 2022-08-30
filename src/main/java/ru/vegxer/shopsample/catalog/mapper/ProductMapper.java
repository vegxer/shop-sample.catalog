package ru.vegxer.shopsample.catalog.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import ru.vegxer.shopsample.catalog.dto.request.ProductPostRequest;
import ru.vegxer.shopsample.catalog.dto.request.ProductPutRequest;
import ru.vegxer.shopsample.catalog.dto.response.ProductResponse;
import ru.vegxer.shopsample.catalog.dto.response.ProductShortResponse;
import ru.vegxer.shopsample.catalog.entity.Attachment;
import ru.vegxer.shopsample.catalog.entity.Product;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    @Mapping(target = "imageThumbnailPaths", source = "attachments", qualifiedByName = "extractThumbnailPath")
    ProductShortResponse mapToShortResponse(Product product);

    @Mappings({
        @Mapping(target = "imageThumbnailPaths", source = "attachments", qualifiedByName = "extractThumbnailPath"),
        @Mapping(target = "imagePaths", source = "attachments", qualifiedByName = "extractPath")
    })
    ProductResponse mapToResponse(Product product);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "category", ignore = true),
        @Mapping(target = "attachments", ignore = true)
    })
    Product mapToEntity(ProductPostRequest product);

    @Mappings({
        @Mapping(target = "category", ignore = true),
        @Mapping(target = "attachments", ignore = true)
    })
    Product mapToEntity(ProductPutRequest product);

    @Named("extractThumbnailPath")
    default String extractThumbnailPath(final Attachment attachment) {
        return attachment == null ? null : attachment.getThumbnailPath();
    }

    @Named("extractPath")
    default String extractPath(final Attachment attachment) {
        return attachment == null ? null : attachment.getPath();
    }
}
