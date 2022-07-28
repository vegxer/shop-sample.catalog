package ru.vegxer.shopsample.catalog.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryPostRequest {
    @NonNull
    private String name;
    private Long parentId;
    private List<Long> childrenIds;
}
