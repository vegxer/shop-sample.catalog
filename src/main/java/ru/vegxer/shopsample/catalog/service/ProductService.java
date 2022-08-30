package ru.vegxer.shopsample.catalog.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.vegxer.shopsample.catalog.dto.request.ProductPostRequest;
import ru.vegxer.shopsample.catalog.dto.request.ProductPutRequest;
import ru.vegxer.shopsample.catalog.dto.response.PathResponse;
import ru.vegxer.shopsample.catalog.dto.response.ProductResponse;
import ru.vegxer.shopsample.catalog.dto.response.ProductShortResponse;
import ru.vegxer.shopsample.catalog.entity.Attachment;
import ru.vegxer.shopsample.catalog.entity.Product;
import ru.vegxer.shopsample.catalog.exception.BadRequestException;
import ru.vegxer.shopsample.catalog.mapper.ProductMapper;
import ru.vegxer.shopsample.catalog.repository.AttachmentRepository;
import ru.vegxer.shopsample.catalog.repository.CategoryRepository;
import ru.vegxer.shopsample.catalog.repository.ProductRepository;
import ru.vegxer.shopsample.catalog.util.StorageUtil;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final AttachmentRepository attachmentRepository;
    private final FileStorageService storageService;
    private final GeneralCategoryService generalCategoryService;

    @Transactional
    public long createProduct(ProductPostRequest productRequest) {
        val productEntity = productMapper.mapToEntity(productRequest);
        setProductCategory(productEntity, productRequest.getCategoryId());
        return productRepository.save(productEntity)
            .getId();
    }

    @Transactional
    public long updateProduct(ProductPutRequest productRequest) {
        val foundEntity = productRepository.findById(productRequest.getId())
            .orElseThrow(() -> new EntityNotFoundException(String.format("Товар с id %d не найден", productRequest.getId())));

        val productEntity = productMapper.mapToEntity(productRequest);
        setProductCategory(productEntity, productRequest.getCategoryId());
        productEntity.setAttachments(foundEntity.getAttachments());
        return productRepository.save(productEntity)
            .getId();
    }

    @Transactional
    public void addAttachmentToProduct(final long productId, final String filename) {
        productRepository.findById(productId)
            .ifPresentOrElse(product -> {
                    val attachment = Attachment.builder()
                        .path(filename)
                        .thumbnailPath(StorageUtil.buildThumbnailPath(filename))
                        .build();
                    attachmentRepository.save(attachment);
                    product.getAttachments()
                        .add(attachment);
                    productRepository.save(product);
                },
                () -> {
                    throw new EntityNotFoundException(String.format("Товар с id %d не найден", productId));
                });
    }

    @Transactional
    public void deleteProduct(final long productId) {
        productRepository.findById(productId)
            .ifPresentOrElse(product -> {
                    productRepository.delete(product);
                    storageService.deleteAttachmentsFiles(product.getAttachments());
                },
                () -> {
                    throw new EntityNotFoundException(String.format("Товар с id %d не найден", productId));
                });
    }

    @Transactional
    public List<String> deleteProductAttachment(final long productId, @NonNull final String filename) {
        val deletedFiles = new ArrayList<String>();
        productRepository.findById(productId)
            .ifPresentOrElse(product -> {
                    for (int i = 0; i < product.getAttachments().size(); ++i) {
                        if (filename.equals(product.getAttachments().get(i).getPath())
                            || filename.equals(product.getAttachments().get(i).getThumbnailPath())) {
                            deletedFiles.add(product.getAttachments().get(i).getPath());
                            deletedFiles.add(product.getAttachments().get(i).getThumbnailPath());
                            product.getAttachments().remove(i);
                            --i;
                        }
                    }
                    product.getAttachments()
                        .removeIf(attachment -> filename.equals(attachment.getPath())
                            || filename.equals(attachment.getThumbnailPath()));
                    productRepository.save(product);
                },
                () -> {
                    throw new EntityNotFoundException(String.format("Товар с id %d не найден", productId));
                });
        return deletedFiles;
    }

    public PathResponse<List<ProductShortResponse>> getProductList(final long categoryId, final Pageable pageable) {
        return new PathResponse<>(generalCategoryService.buildPathToCategory(categoryId),
            productRepository.findByCategory(categoryId, pageable)
                .stream()
                .map(productMapper::mapToShortResponse)
                .collect(Collectors.toList())
        );
    }

    public PathResponse<ProductResponse> getProduct(final long productId) {
        val foundProduct = productRepository.findById(productId)
            .orElseThrow(() -> new EntityNotFoundException(String.format("Товар с id %d не найден", productId)));

        return new PathResponse<>(generalCategoryService.buildPathToCategory(
            foundProduct.getCategory().getId()),
            productMapper.mapToResponse(foundProduct)
        );
    }

    @Transactional
    public void deleteCategoryProducts(final long categoryId) {
        productRepository.findByCategory(categoryId)
            .forEach(product -> {
                productRepository.delete(product);
                storageService.deleteAttachmentsFiles(product.getAttachments());
            });
    }

    private void setProductCategory(final Product product, final long categoryId) {
        val categoryEntity = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new EntityNotFoundException(String.format("Категория с id %d не найдена", categoryId)));
        if (!CollectionUtils.isEmpty(categoryEntity.getChildren())) {
            throw new BadRequestException("Нельзя добавлять товары к неконечной категории");
        }
        product.setCategory(categoryEntity);
    }
}
