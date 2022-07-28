package ru.vegxer.shopsample.catalog.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttachmentRequest {
    private Long categoryId;
    private Long productId;
    @NonNull
    private MultipartFile attachment;
}
