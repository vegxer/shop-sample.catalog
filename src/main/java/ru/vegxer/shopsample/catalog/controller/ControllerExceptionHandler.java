package ru.vegxer.shopsample.catalog.controller;

import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import ru.vegxer.shopsample.catalog.dto.response.ErrorResponse;
import ru.vegxer.shopsample.catalog.exception.BadRequestException;
import ru.vegxer.shopsample.catalog.exception.FileNotFoundException;
import ru.vegxer.shopsample.catalog.exception.StorageException;

import javax.persistence.EntityNotFoundException;

@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler({MaxUploadSizeExceededException.class, FileSizeLimitExceededException.class})
    public ResponseEntity<ErrorResponse> handleMaxSizeException() {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(
            new ErrorResponse(HttpStatus.PAYLOAD_TOO_LARGE.value(), "Размер файла слишком большой для обработки"));
    }

    @ExceptionHandler({EntityNotFoundException.class, FileNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(Exception notFoundException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            new ErrorResponse(HttpStatus.NOT_FOUND.value(), notFoundException.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(Exception badRequestException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            new ErrorResponse(HttpStatus.BAD_REQUEST.value(), badRequestException.getMessage()));
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<ErrorResponse> handleStorageException(Exception storageException) {
        return ResponseEntity.status(HttpStatus.INSUFFICIENT_STORAGE).body(
            new ErrorResponse(HttpStatus.INSUFFICIENT_STORAGE.value(), storageException.getMessage()));
    }

    @ExceptionHandler(AuthorizationServiceException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationException(Exception authException) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), authException.getMessage()));
    }
}
