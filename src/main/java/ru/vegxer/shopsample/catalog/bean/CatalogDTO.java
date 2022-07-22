package ru.vegxer.shopsample.catalog.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CatalogDTO {
    private String name;
    private BigDecimal price;
}
