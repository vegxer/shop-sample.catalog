package ru.vegxer.shopsample.catalog.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.vegxer.shopsample.catalog.dto.request.CategoryPostRequest;
import ru.vegxer.shopsample.catalog.dto.request.CategoryPutRequest;
import ru.vegxer.shopsample.catalog.dto.response.ItemsResponse;
import ru.vegxer.shopsample.catalog.dto.response.PagedResponse;
import ru.vegxer.shopsample.catalog.dto.response.PathResponse;
import ru.vegxer.shopsample.catalog.dto.response.CategoryResponse;
import ru.vegxer.shopsample.catalog.entity.Attachment;
import ru.vegxer.shopsample.catalog.entity.Category;
import ru.vegxer.shopsample.catalog.exception.BadRequestException;
import ru.vegxer.shopsample.catalog.mapper.CategoryMapper;
import ru.vegxer.shopsample.catalog.repository.AttachmentRepository;
import ru.vegxer.shopsample.catalog.repository.CategoryRepository;
import ru.vegxer.shopsample.catalog.util.StorageUtil;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final AttachmentRepository attachmentRepository;
    private final CategoryMapper categoryMapper;
    private final FileStorageService storageService;
    private final ProductService productService;
    private final GeneralCategoryService generalCategoryService;

    @Transactional
    public long createCategory(final CategoryPostRequest categoryRequest) {
        val categoryEntity = categoryMapper.mapToEntity(categoryRequest);
        setCategoryRelatives(categoryEntity, categoryRequest);
        return categoryRepository.save(categoryEntity)
            .getId();
    }

    @Transactional
    public long updateCategory(final CategoryPutRequest categoryRequest) {
        val categoryEntity = categoryRepository.findById(categoryRequest.getId())
            .orElseThrow(() -> new EntityNotFoundException(String.format("Категория с id %d не найдена", categoryRequest.getId())));
        categoryEntity.setName(categoryRequest.getName());
        setCategoryRelatives(categoryEntity, categoryRequest);
        return categoryRepository.save(categoryEntity)
            .getId();
    }

    public PagedResponse<CategoryResponse> getPrimalCategories(final Pageable page) {
        val pagedCategories = categoryRepository.findPrimalCategories(page);
        return new PagedResponse<>(
            pagedCategories.getContent()
                .stream()
                .map(categoryMapper::mapToResponse)
                .collect(Collectors.toList()),
            pagedCategories.getTotalPages()
        );
    }

    public PathResponse<ItemsResponse<PagedResponse<CategoryResponse>>> getSubcategories(final long categoryId, final Pageable page) {
        val pagesCategories = categoryRepository.findSubcategories(categoryId, page);
        return new PathResponse<>(generalCategoryService.buildPathToCategory(categoryId),
            new ItemsResponse<>(
                categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new EntityNotFoundException(String.format("Категория с id %d не найдена", categoryId)))
                    .getName(),
                new PagedResponse<>(
                    pagesCategories.getContent()
                        .stream()
                        .map(categoryMapper::mapToResponse)
                        .collect(Collectors.toList()),
                    pagesCategories.getTotalPages()
                )
            )
        );
    }

    @Transactional
    public void replaceCategoryAttachment(final long categoryId, final String filename) {
        categoryRepository.findById(categoryId)
            .ifPresentOrElse(category -> {
                    storageService.deleteAttachmentFiles(category.getAttachment());
                    val attachment = Attachment.builder()
                        .path(filename)
                        .thumbnailPath(StorageUtil.buildThumbnailPath(filename))
                        .build();
                    attachmentRepository.save(attachment);
                    category.setAttachment(attachment);
                    categoryRepository.save(category);
                },
                () -> {
                    throw new EntityNotFoundException(String.format("Категория с id %d не найдена", categoryId));
                });
    }

    @Transactional
    public void deleteCategoryAttachment(final long categoryId) {
        categoryRepository.findById(categoryId)
            .ifPresentOrElse(category -> {
                    storageService.deleteAttachmentFiles(category.getAttachment());
                    category.setAttachment(null);
                    categoryRepository.save(category);
                },
                () -> {
                    throw new EntityNotFoundException(String.format("Категория с id %d не найдена", categoryId));
                });
    }

    @Transactional
    public void deleteCategory(final long categoryId) {
        categoryRepository.findById(categoryId)
            .ifPresentOrElse(category -> {
                    productService.deleteCategoryProducts(categoryId);
                    categoryRepository.delete(category);
                    storageService.deleteAttachmentFiles(category.getAttachment());
                },
                () -> {
                    throw new EntityNotFoundException(String.format("Категория с id %d не найдена", categoryId));
                });
    }

    private void setCategoryRelatives(Category categoryEntity, final CategoryPostRequest categoryRequest) {
        if (categoryRequest.getChildrenIds() != null) {
            if (categoryRequest.getChildrenIds().contains(categoryRequest.getParentId())) {
                throw new BadRequestException("Дочерние категории не могут содержать родительскую");
            }
            if (categoryRequest.getChildrenIds().contains(categoryEntity.getId())) {
                throw new BadRequestException("Дочерние категории не могут содержать текущую");
            }
            if (categoryEntity.getId() != null && categoryEntity.getId().equals(categoryRequest.getParentId())) {
                throw new BadRequestException("Родительская категория не может быть равна текущей");
            }
        }

        if (categoryRequest.getParentId() != null) {
            val parentCategory = categoryRepository.findById(categoryRequest.getParentId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Категория с id %d не найдена", categoryRequest.getParentId())));
            categoryEntity = categoryRepository.saveAndFlush(categoryEntity);
            for (val p : productService.getAllProducts(parentCategory.getId())) {
                productService.setProductCategory(p, categoryEntity.getId());
            }
            categoryEntity.setParent(parentCategory);
        }
        if (categoryRequest.getChildrenIds() != null) {
            if (categoryEntity.getChildren() == null) {
                categoryEntity.setChildren(new ArrayList<>());
            }
            for (val childId : categoryRequest.getChildrenIds()) {
                val childCategory = categoryRepository.findById(childId)
                    .orElseThrow(() -> new EntityNotFoundException(String.format("Категория с id %d не найдена", categoryRequest.getParentId())));
                childCategory.setParent(categoryEntity);
                categoryEntity.getChildren()
                    .add(childCategory);
            }
            if (categoryEntity.getId() != null) {
                productService.deleteCategoryProducts(categoryEntity.getId());
            }
        }
    }
}
