package ru.vegxer.shopsample.catalog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vegxer.shopsample.catalog.dto.request.ProductPostRequest;
import ru.vegxer.shopsample.catalog.dto.request.ProductPutRequest;
import ru.vegxer.shopsample.catalog.dto.response.ProductResponse;
import ru.vegxer.shopsample.catalog.dto.response.ProductShortResponse;
import ru.vegxer.shopsample.catalog.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
    final ProductService productService;

    @PostMapping
    @Operation(summary = "Создать товар")
    @ApiResponse(description = "ID созданного товара", responseCode = "200")
    public ResponseEntity<Long> createProduct(@io.swagger.v3.oas.annotations.parameters.RequestBody(
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductPostRequest.class)))
                                                  @RequestBody ProductPostRequest productPostRequest) {
        return ResponseEntity
            .ok(productService.createProduct(productPostRequest));
    }

    @PutMapping
    @Operation(summary = "Обновить товар")
    @ApiResponse(description = "ID обновлённого товара", responseCode = "200")
    public ResponseEntity<Long> updateProduct(@io.swagger.v3.oas.annotations.parameters.RequestBody(
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProductPutRequest.class)))
                                              @RequestBody ProductPutRequest productPutRequest) {
        return ResponseEntity
            .ok(productService.updateProduct(productPutRequest));
    }

    @GetMapping("/list")
    @Operation(summary = "Получить список товаров")
    @ApiResponse(description = "Список товаров", content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = ProductShortResponse.class, type = "array")), responseCode = "200")
    @ResponseBody
    public ResponseEntity<List<ProductShortResponse>> getProductList(@RequestParam(required = false, defaultValue = "20") final int pageSize,
                                                                     @RequestParam(required = false, defaultValue = "1") final int pageNumber) {
        return ResponseEntity
            .ok(productService.getProductList(Pageable.ofSize(pageSize).withPage(pageNumber - 1)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить товар")
    @ApiResponse(description = "Список товаров", content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = ProductResponse.class)), responseCode = "200")
    @ResponseBody
    public ResponseEntity<ProductResponse> getProductList(@PathVariable final long id) {
        return ResponseEntity
            .ok(productService.getProduct(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить товар")
    public ResponseEntity<?> deleteProduct(@PathVariable final long id) {
        productService.deleteProduct(id);
        return ResponseEntity
            .ok()
            .build();
    }
}