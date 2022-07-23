package ru.vegxer.shopsample.catalog.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttachmentCategoryRequest {
    private Long categoryId;
    private MultipartFile attachment;
}
