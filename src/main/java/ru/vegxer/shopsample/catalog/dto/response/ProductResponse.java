package ru.vegxer.shopsample.catalog.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse extends ProductShortResponse {
    private String description;
    private Long amount;
    private List<String> paths;
}
