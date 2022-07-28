package ru.vegxer.shopsample.catalog.dto.response;

import lombok.*;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse extends ProductShortResponse {
    private String description;
    @NonNull
    private Long amount;
    private List<String> paths;
}
