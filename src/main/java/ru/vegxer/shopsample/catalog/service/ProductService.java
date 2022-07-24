package ru.vegxer.shopsample.catalog.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.vegxer.shopsample.catalog.dto.request.ProductPostRequest;
import ru.vegxer.shopsample.catalog.dto.request.ProductPutRequest;
import ru.vegxer.shopsample.catalog.dto.response.ProductResponse;
import ru.vegxer.shopsample.catalog.dto.response.ProductShortResponse;
import ru.vegxer.shopsample.catalog.entity.Attachment;
import ru.vegxer.shopsample.catalog.mapper.ProductMapper;
import ru.vegxer.shopsample.catalog.repository.AttachmentRepository;
import ru.vegxer.shopsample.catalog.repository.CategoryRepository;
import ru.vegxer.shopsample.catalog.repository.ProductRepository;
import ru.vegxer.shopsample.catalog.util.StorageUtil;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
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

    @Transactional
    public long createProduct(ProductPostRequest productRequest) {
        val productEntity = productMapper.mapToEntity(productRequest);
        productEntity.setCategory(categoryRepository.findById(productRequest.getCategoryId())
            .orElseThrow(() -> new EntityNotFoundException(String.format("Категория с id %d не найдена", productRequest.getCategoryId()))));
        return productRepository.save(productEntity)
            .getId();
    }

    @Transactional
    public long updateProduct(ProductPutRequest productRequest) {
        val foundEntity = productRepository.findById(productRequest.getId())
            .orElseThrow(() -> new EntityNotFoundException(String.format("Товар с id %d не найден", productRequest.getId())));

        val productEntity = productMapper.mapToEntity(productRequest);
        productEntity.setCategory(categoryRepository.findById(productRequest.getCategoryId())
            .orElseThrow(() -> new EntityNotFoundException(String.format("Категория с id %d не найдена", productRequest.getCategoryId()))));
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
                    product.getAttachments()
                        .forEach(attachment -> {
                            if (attachment != null) {
                                if (attachment.getPath() != null) {
                                    storageService.deleteResource(attachment.getPath());
                                }
                                if (attachment.getThumbnailPath() != null) {
                                    storageService.deleteResource(attachment.getThumbnailPath());
                                }
                            }
                        });
                },
                () -> {
                    throw new EntityNotFoundException(String.format("Товар с id %d не найден", productId));
                });
    }

    public List<ProductShortResponse> getProductList(final Pageable pageable) {
        return productRepository.findAll(pageable)
            .stream()
            .map(productMapper::mapToShortResponse)
            .collect(Collectors.toList());
    }

    public ProductResponse getProduct(final long productId) {
        return productMapper.mapToResponse(
            productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Товар с id %d не найден", productId)))
        );
    }
}
