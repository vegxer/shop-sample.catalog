package ru.vegxer.shopsample.catalog.controller;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vegxer.shopsample.catalog.dto.request.AttachmentRequest;
import ru.vegxer.shopsample.catalog.dto.response.ErrorResponse;
import ru.vegxer.shopsample.catalog.service.CategoryService;
import ru.vegxer.shopsample.catalog.service.FileStorageService;
import ru.vegxer.shopsample.catalog.service.ProductService;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@RequestMapping("/attachment")
@RequiredArgsConstructor
public class FileStorageController {
    final FileStorageService fileStorageService;
    final CategoryService categoryService;
    final ProductService productService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE}, produces = {MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity<?> uploadAttachment(@ModelAttribute AttachmentRequest attachmentRequest) {
        val filename = fileStorageService.store(attachmentRequest.getAttachment());
        if (attachmentRequest.getCategoryId() != null) {
            categoryService.replaceCategoryAttachment(attachmentRequest.getCategoryId(), filename);
        } else if (attachmentRequest.getProductId() != null) {
            productService.addAttachmentToProduct(attachmentRequest.getProductId(), filename);
        } else {
            return ResponseEntity.ok(new ErrorResponse(BAD_REQUEST.value(), "Необходимо либо productId, либо categoryId"));
        }
        return ResponseEntity.ok(filename);
    }

    @DeleteMapping(path = "/product/{id}")
    public ResponseEntity<?> deleteAttachment(@PathVariable long id, @RequestParam String filename) {
        fileStorageService.deleteResources(
            productService.deleteProductAttachment(id, filename)
        );
        return ResponseEntity
            .ok()
            .build();
    }

    @DeleteMapping(path = "/category/{id}")
    public ResponseEntity<?> deleteAttachment(@PathVariable long id) {
        categoryService.deleteCategoryAttachment(id);
        return ResponseEntity
            .ok()
            .build();
    }

    @GetMapping(path = "/{filename}", produces = {MediaType.APPLICATION_OCTET_STREAM_VALUE})
    @ResponseBody
    public ResponseEntity<Resource> downloadAttachment(@PathVariable String filename) {
        val resource = fileStorageService.loadAsResource(filename);
        return ResponseEntity
            .ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=\"%s\"",
                resource.getFilename().substring(resource.getFilename().indexOf('_') + 1)))
            .body(resource);
    }
}
