package ru.vegxer.shopsample.catalog.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.vegxer.shopsample.catalog.dto.request.CategoryPostRequest;
import ru.vegxer.shopsample.catalog.dto.request.CategoryPutRequest;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final AttachmentRepository attachmentRepository;
    private final CategoryMapper categoryMapper;
    private final FileStorageService storageService;

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

    public List<CategoryResponse> getPrimalCategories(final Pageable page) {
        return categoryRepository.findPrimalCategories(page)
            .stream()
            .map(categoryMapper::mapToResponse)
            .collect(Collectors.toList());
    }

    public List<CategoryResponse> getSubcategories(final long categoryId, final Pageable page) {
        return categoryRepository.findSubcategories(categoryId, page)
            .stream()
            .map(categoryMapper::mapToResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public void replaceCategoryAttachment(final long categoryId, final String filename) {
        categoryRepository.findById(categoryId)
            .ifPresentOrElse(category -> {
                    if (category.getAttachment() != null) {
                        if (category.getAttachment().getThumbnailPath() != null) {
                            storageService.deleteResource(category.getAttachment().getThumbnailPath());
                        }
                        if (category.getAttachment().getPath() != null) {
                            storageService.deleteResource(category.getAttachment().getPath());
                        }
                    }
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
    public void deleteCategory(final long categoryId) {
        categoryRepository.findById(categoryId)
            .ifPresentOrElse(category -> {
                    categoryRepository.delete(category);
                    if (category.getAttachment() != null) {
                        if (category.getAttachment().getPath() != null) {
                            storageService.deleteResource(category.getAttachment().getPath());
                        }
                        if (category.getAttachment().getThumbnailPath() != null) {
                            storageService.deleteResource(category.getAttachment().getThumbnailPath());
                        }
                    }
                },
                () -> {
                    throw new EntityNotFoundException(String.format("Категория с id %d не найдена", categoryId));
                });
    }

    private void setCategoryRelatives(final Category category, final CategoryPostRequest categoryRequest) {
        if (categoryRequest.getChildrenIds() != null) {
            if (categoryRequest.getChildrenIds().contains(categoryRequest.getParentId())) {
                throw new BadRequestException("Дочерние категории не могут содержать родительскую");
            }
            if (categoryRequest.getChildrenIds().contains(category.getId())) {
                throw new BadRequestException("Дочерние категории не могут содержать текущую");
            }
            if (category.getId() != null && category.getId().equals(categoryRequest.getParentId())) {
                throw new BadRequestException("Родительская категория не может быть равна текущей");
            }
        }

        if (categoryRequest.getParentId() != null) {
            category.setParent(categoryRepository.findById(categoryRequest.getParentId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Категория с id %d не найдена", categoryRequest.getParentId()))));
        }
        if (categoryRequest.getChildrenIds() != null) {
            if (category.getChildren() == null) {
                category.setChildren(new ArrayList<>());
            }
            categoryRequest.getChildrenIds()
                .forEach(childId -> {
                    val childCategory = categoryRepository.findById(childId)
                        .orElseThrow(() -> new EntityNotFoundException(String.format("Категория с id %d не найдена", categoryRequest.getParentId())));
                    childCategory.setParent(category);
                    category.getChildren()
                        .add(childCategory);
                });
        }
    }
}
