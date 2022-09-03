package ru.vegxer.shopsample.catalog.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemsResponse<T> {
    private String name;
    private List<T> items;
}
