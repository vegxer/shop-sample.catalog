package ru.vegxer.shopsample.catalog.controller;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vegxer.shopsample.catalog.dto.request.AttachmentCategoryRequest;
import ru.vegxer.shopsample.catalog.dto.request.AttachmentProductRequest;
import ru.vegxer.shopsample.catalog.dto.request.CategoryPostRequest;
import ru.vegxer.shopsample.catalog.service.CategoryService;
import ru.vegxer.shopsample.catalog.service.FileStorageService;

@RestController
@RequestMapping("/attachment")
@RequiredArgsConstructor
public class FileStorageController {
    final FileStorageService fileStorageService;
    final CategoryService categoryService;

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<String> uploadCategoryAttachment(@ModelAttribute AttachmentCategoryRequest attachmentRequest) {
        val filename = fileStorageService.store(attachmentRequest.getAttachment());
        categoryService.addAttachmentToCategory(attachmentRequest.getCategoryId(), filename);
        return ResponseEntity.ok(filename);
    }

    /*@PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity uploadProductAttachment(@ModelAttribute AttachmentProductRequest attachmentRequest) {
        return ResponseEntity.ok(new CategoryPostRequest());
    }*/
}
