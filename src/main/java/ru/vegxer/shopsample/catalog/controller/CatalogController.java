package ru.vegxer.shopsample.catalog.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.vegxer.shopsample.catalog.bean.CatalogDTO;

import java.math.BigDecimal;

@RestController
@RequestMapping("/catalog")
public class CatalogController {

    @GetMapping
    @ResponseBody
    @ApiResponse(description = "хуйня")
    public ResponseEntity<CatalogDTO> getCatalog() {
        return ResponseEntity.ok(new CatalogDTO("123", new BigDecimal(12)));
    }
}
