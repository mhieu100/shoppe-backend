package com.project.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDTO {
    private int id;
    private String name;
    private String descriptions;
    private BigDecimal price;
    private int stockQuantity;
    private String categoryName;
    private String sellerName;

    private List<ProductImageDTO> images;
}
