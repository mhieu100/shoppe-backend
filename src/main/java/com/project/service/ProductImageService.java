package com.project.service;

import com.project.exception.ExistException;
import com.project.model.ProductImage;
import com.project.repository.ProductImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductImageService {
    private final ProductImageRepository productImageRepository;

    private final CloudinaryService cloudinaryService;

    public ProductImage replaceImage(String imgId, MultipartFile image) throws ExistException {
        ProductImage productImage = productImageRepository.findByPublicId(imgId);

        if(productImage == null){
            throw new ExistException("Not found product image");
        }

        try{
            cloudinaryService.delete(productImage.getPublicId());
        } catch (IOException e) {
            throw new RuntimeException("Delete product image failed");
        }

        try{
            Map uploadResult = cloudinaryService.upload(image,"products");

            productImage.setImageUrl(uploadResult.get("url").toString());
            productImage.setPublicId(uploadResult.get("public_id").toString());
            productImage.setCreated_at(new Date());
            productImageRepository.save(productImage);
        } catch (IOException e) {
            throw new RuntimeException("Upload product image failed");
        }

        return productImage;
    }

    public void deleteImage(int imgId) throws ExistException {
        ProductImage productImage = productImageRepository.findById(imgId).orElseThrow(() -> new ExistException("Not found product image"));

        try{
            cloudinaryService.delete(productImage.getPublicId());
        } catch (IOException e) {
            throw new RuntimeException("Delete product image failed");
        }

        productImageRepository.delete(productImage);
    }
}
