package ru.vegxer.shopsample.catalog.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import ru.vegxer.shopsample.catalog.entity.Product;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductPostRequest {
    @NonNull
    private String name;
    private String description;
    @NonNull
    private BigDecimal price;
    @NonNull
    private Long amount;
    private Product.ProductState state;
    @NonNull
    private Long categoryId;
}
