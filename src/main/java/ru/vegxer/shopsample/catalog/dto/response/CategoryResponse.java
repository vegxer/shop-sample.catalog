package ru.vegxer.shopsample.catalog.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryResponse {
    @NonNull
    private Long id;
    @NonNull
    private String name;
    private boolean hasChildren;
    private String thumbnailPath;
    private String path;
}