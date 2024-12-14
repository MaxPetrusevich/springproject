package com.example.controller;

import com.example.dto.ProductDto;
import com.example.mapper.EntityMapper;
import com.example.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController extends BaseController {

    private final ProductService productService;
    private final EntityMapper mapper;

    @GetMapping
    public ResponseEntity<Page<ProductDto>> findAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) Long organisationId,
            @PageableDefault() Pageable pageable
    ) {
        return ResponseEntity.ok(
                        productService.findAll(search, category, active, organisationId, pageable).map(mapper::toDto)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(
                mapper.toDto(productService.findById(id))
        );
    }

    @PreAuthorize("hasRole('ORGANIZER')")
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto productDto) {
        return ResponseEntity.ok(
                mapper.toDto(productService.create(productDto))
        );
    }

    @PreAuthorize("hasRole('ORGANIZER') and @securityService.isProductOwner(#id, principal)")
    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductDto productDto
    ) {
        return ResponseEntity.ok(
                mapper.toDto(productService.update(id, productDto))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }
}