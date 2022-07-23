package ru.vegxer.shopsample.catalog.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.vegxer.shopsample.catalog.entity.Product;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductShortResponse {
    private Long id;
    private String name;
    private BigDecimal price;
    private Product.ProductState state;
    private List<String> thumbnailPaths;
}
