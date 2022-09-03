package ru.vegxer.shopsample.catalog.service;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.stereotype.Service;
import ru.vegxer.shopsample.catalog.dto.response.CategoryShortResponse;
import ru.vegxer.shopsample.catalog.mapper.CategoryMapper;
import ru.vegxer.shopsample.catalog.repository.CategoryRepository;

import javax.persistence.EntityNotFoundException;
import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GeneralCategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public List<CategoryShortResponse> buildPathToCategory(final long categoryId) {
        var category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new EntityNotFoundException(String.format("Категория с id %d не найдена", categoryId)));

        val path = new LinkedList<CategoryShortResponse>();
        while (category.getParent() != null) {
            category = category.getParent();
            path.add(0, categoryMapper.mapToShortResponse(category));
        }

        return path;
    }
}
