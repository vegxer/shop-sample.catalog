package ru.vegxer.shopsample.catalog.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.vegxer.shopsample.catalog.entity.Product;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductPostRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private Long amount;
    private Product.ProductState state;
    private Long categoryId;
}
