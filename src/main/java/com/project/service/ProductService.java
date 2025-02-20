package com.project.service;

import com.project.dto.Pagination;
import com.project.dto.ProductDTO;
import com.project.dto.ProductImageDTO;
import com.project.dto.ProductResponseDTO;
import com.project.exception.ExistException;
import com.project.model.Category;
import com.project.model.Product;
import com.project.model.ProductImage;
import com.project.model.User;
import com.project.repository.CategoryRepository;
import com.project.repository.ProductImageRepository;
import com.project.repository.ProductRepository;
import com.project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    private final ProductImageRepository productImageRepository;

    private final CloudinaryService cloudinaryService;

    private final CategoryRepository categoryRepository;

    private final UserRepository userRepository;

    public ProductResponseDTO addProduct(ProductDTO productDTO, MultipartFile[] images) {

        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        User user = userRepository.findById(7)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        System.out.println(Arrays.toString(images));
        Product product = new Product();

        product.setCategory(category);
        product.setUser(user);
        product.setName(productDTO.getName());
        product.setDescriptions(productDTO.getDescriptions());
        product.setPrice(productDTO.getPrice());
        product.setStockQuantity(productDTO.getStockQuantity());
        product.setCreate_at(new Date());

        Product savedProduct = productRepository.save(product);

        for (MultipartFile image : images) {
            try {
                Map uploadResult = cloudinaryService.upload(image, "products");

                ProductImage productImage = new ProductImage();
                productImage.setProduct(savedProduct);
                productImage.setImageUrl(uploadResult.get("url").toString());
                productImage.setPublicId(uploadResult.get("public_id").toString());
                productImage.setIsPrimary(false);
                productImage.setCreated_at(new Date());

                productImageRepository.save(productImage);
            } catch (IOException e) {
                throw new RuntimeException("Failed to add image to product");
            }
        }
        return convertToDTO(savedProduct);
    }

    public ProductResponseDTO updateProduct(Integer id, ProductDTO productDTO, MultipartFile[] images) throws ExistException {
        Product product = productRepository.findById(id).orElseThrow(() -> new ExistException("Product not found"));

        Category category = categoryRepository.findById(productDTO.getCategoryId()).orElseThrow(() -> new ExistException("Category not found"));

        product.setName(productDTO.getName());
        product.setPrice(productDTO.getPrice());
        product.setDescriptions(productDTO.getDescriptions());
        product.setStockQuantity(productDTO.getStockQuantity());
        product.setCategory(category);

        User user = userRepository.findById(7)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        product.setUser(user);

        product.setUpdate_at(new Date());

        Product updatedProduct = productRepository.save(product);

        if (images != null) {
            for (MultipartFile image : images) {
                try {
                    Map uploadResult = cloudinaryService.upload(image, "products");

                    ProductImage productImage = new ProductImage();
                    productImage.setProduct(updatedProduct);
                    productImage.setImageUrl(uploadResult.get("url").toString());
                    productImage.setPublicId(uploadResult.get("public_id").toString());
                    productImage.setIsPrimary(false);
                    productImage.setCreated_at(new Date());
                    productImageRepository.save(productImage);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to add image to product");
                }
            }
        }

        return convertToDTO(updatedProduct);
    }

    public Pagination<ProductResponseDTO> getAllProducts(Specification<Product> specification, Pageable pageable) {
        Page<Product> pageProduct = productRepository.findAll(specification, pageable);

        Pagination<ProductResponseDTO> pagination = new Pagination<>();
        Pagination.Meta meta = new Pagination.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(pageProduct.getTotalPages());
        meta.setTotal(pageProduct.getTotalElements());

        List<ProductResponseDTO> listProducts = pageProduct.getContent().stream()
                .map(this::convertToDTO)
                .toList();

        pagination.setMeta(meta);
        pagination.setResult(listProducts);

        return pagination;
    }


    public void deleteProductById(int id) throws ExistException, IOException {
        Product product = productRepository.findById(id).orElseThrow(() -> new ExistException("Such product does not exist"));

        List<ProductImage> productImages = productImageRepository.findByProduct(product);

        if (!productImages.isEmpty()) {
            for (ProductImage productImage : productImages) {
                String cloudinaryImageId = productImage.getPublicId();
                if (cloudinaryImageId != null) {
                    try {
                        cloudinaryService.delete(cloudinaryImageId);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to delete image from product");
                    }
                }
            }
        }

        productImageRepository.deleteAll(productImages);

        productRepository.delete(product);
    }

    public ProductResponseDTO getProduct(int id) throws ExistException {
        Product product = productRepository.findById(id).orElseThrow(() -> new ExistException("Product does not exist"));
        return convertToDTO(product);
    }

    public ProductResponseDTO convertToDTO(Product product) {
        ProductResponseDTO productResponseDTO = new ProductResponseDTO();
        productResponseDTO.setId(product.getId());
        productResponseDTO.setName(product.getName());
        productResponseDTO.setDescriptions(product.getDescriptions());
        productResponseDTO.setPrice(product.getPrice());
        productResponseDTO.setStockQuantity(product.getStockQuantity());

        if (product.getCategory() != null) {
            productResponseDTO.setCategoryName(product.getCategory().getName());
        }

        productResponseDTO.setSellerName(product.getUser().getFullname());

        List<ProductImageDTO> images = productImageRepository.findByProduct(product).stream()
                .map(image -> new ProductImageDTO(image.getId(), image.getImageUrl()))
                .collect(Collectors.toList());

        productResponseDTO.setImages(images);
        return productResponseDTO;
    }
}
