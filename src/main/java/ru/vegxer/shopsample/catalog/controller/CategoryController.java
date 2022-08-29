package ru.vegxer.shopsample.catalog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vegxer.shopsample.catalog.dto.request.CategoryPostRequest;
import ru.vegxer.shopsample.catalog.dto.request.CategoryPutRequest;
import ru.vegxer.shopsample.catalog.dto.response.CategoryResponse;
import ru.vegxer.shopsample.catalog.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {
    final CategoryService categoryService;


    @PostMapping
    @Operation(summary = "Создать категорию")
    @ApiResponse(description = "ID созданной категории", responseCode = "200")
    public ResponseEntity<Long> createCategory(@io.swagger.v3.oas.annotations.parameters.RequestBody(
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryPostRequest.class)))
                                               @RequestBody CategoryPostRequest categoryPostRequest) {
        return ResponseEntity
            .ok(categoryService.createCategory(categoryPostRequest));
    }

    @PutMapping
    @Operation(summary = "Обновить категорию")
    @ApiResponses({
        @ApiResponse(description = "ID обновлённой категории", responseCode = "200"),
        @ApiResponse(description = "Ошибка: категория с данным ID не найдена", responseCode = "404")
    })
    public ResponseEntity<Long> updateCategory(@io.swagger.v3.oas.annotations.parameters.RequestBody(
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = CategoryPutRequest.class)))
                                               @RequestBody CategoryPutRequest categoryPutRequest) {
        return ResponseEntity
            .ok(categoryService.updateCategory(categoryPutRequest));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить категорию по ID")
    public ResponseEntity<?> deleteCategory(@PathVariable final long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity
            .ok()
            .build();
    }

    @GetMapping("/primal")
    @Operation(summary = "Получить первичные категории")
    @ApiResponse(description = "Список категорий", content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = CategoryResponse.class, type = "array")), responseCode = "200")
    @ResponseBody
    public ResponseEntity<List<CategoryResponse>> getPrimalCategories(@RequestParam(required = false, defaultValue = "10") final int pageSize,
                                                                      @RequestParam(required = false, defaultValue = "1") final int pageNumber) {
        return ResponseEntity
            .ok(categoryService.getPrimalCategories(Pageable.ofSize(pageSize).withPage(pageNumber - 1)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить подкатегории категории")
    @ApiResponse(description = "Список категорий", content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = CategoryResponse.class, type = "array")), responseCode = "200")
    @ResponseBody
    public ResponseEntity<List<CategoryResponse>> getSubcategories(@PathVariable final long id,
                                                                   @RequestParam(required = false, defaultValue = "10") final int pageSize,
                                                                   @RequestParam(required = false, defaultValue = "1") final int pageNumber) {
        return ResponseEntity
            .ok(categoryService.getSubcategories(id, Pageable.ofSize(pageSize).withPage(pageNumber - 1)));
    }
}
