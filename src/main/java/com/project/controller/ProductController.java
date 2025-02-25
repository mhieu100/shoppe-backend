package com.project.controller;

import com.project.dto.Pagination;
import com.project.dto.ProductDTO;
import com.project.dto.ProductResponseDTO;
import com.project.exception.ExistException;
import com.project.model.Product;
import com.project.model.ProductImage;
import com.project.repository.ProductImageRepository;
import com.project.repository.ProductRepository;
import com.project.service.ProductImageService;
import com.project.service.ProductService;
import com.turkraft.springfilter.boot.Filter;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    @Autowired
    private final ProductService productService;

    @Autowired
    private final ProductImageService productImageService;

    @Autowired
    private final ProductImageRepository productImageRepository;

    @GetMapping
    public ResponseEntity<Pagination<ProductResponseDTO>> getProducts(@Filter Specification<Product> specification,
            Pageable pageable) {
        return ResponseEntity.ok().body(productService.getAllProducts(specification, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> getProduct(@PathVariable Integer id) throws ExistException {
        ProductResponseDTO product = productService.getProduct(id);
        return ResponseEntity.ok().body(product);
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> createProduct(@ModelAttribute ProductDTO productDTO,
            @RequestParam("images") MultipartFile[] images) {

        ProductResponseDTO product = productService.addProduct(productDTO, images);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable("id") int id,
            @ModelAttribute ProductDTO product,
            @RequestParam(value = "images", required = false) MultipartFile[] images) throws ExistException {
        ProductResponseDTO product1 = productService.updateProduct(id, product, images);
        return ResponseEntity.ok().body(product1);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Integer id) throws ExistException, IOException {
        productService.deleteProductById(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Product deleted successfully");
    }

    @PutMapping("/{productId}/replace-image/{imageId}")
    public ResponseEntity<ProductImage> replaceImage(@PathVariable("productId") int productId,
            @PathVariable("imageId") int imageId,
            @RequestParam("image") MultipartFile image) throws ExistException {
        ProductImage productImage = productImageRepository.findById(imageId)
                .orElseThrow(() -> new ExistException("Image not found"));

        ProductImage updateProductImage = productImageService.replaceImage(productImage.getPublicId(), image);
        return ResponseEntity.ok().body(updateProductImage);
    }

    @DeleteMapping("/delete-image/{imageId}")
    public ResponseEntity<?> deleteImage(@PathVariable("imageId") int imageId) throws ExistException {

        productImageService.deleteImage(imageId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Image deleted successfully");
    }
}
