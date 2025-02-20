package com.project.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public Map upload(MultipartFile multipartFile, String path) throws IOException {
        File file = convert(multipartFile);
        try {
            Map result = cloudinary.uploader().upload(file, ObjectUtils.asMap(
                    "folder", "shop/image/" + path
            ));
            System.out.println("URL là: " + result.get("url"));
            return result;
        } finally {
            Files.deleteIfExists(file.toPath());
        }
    }


    public Map delete(String id) throws IOException {
        return cloudinary.uploader().destroy(id, ObjectUtils.emptyMap());
    }

    private File convert(MultipartFile multipartFile) throws IOException {
        File tempFile = Files.createTempFile("upload-", Objects.requireNonNull(multipartFile.getOriginalFilename())).toFile();
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(multipartFile.getBytes());
        }
        return tempFile;
    }
}