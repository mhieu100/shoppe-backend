package com.project.specification;
import org.springframework.data.jpa.domain.Specification;

import com.project.model.Product;

public class ProductSpecification {
    public static Specification<Product> constainTextName(String searchTerm){
        return (root,query, criteriaBuilder) -> {
            if(searchTerm == null || searchTerm.isEmpty()){
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                criteriaBuilder.lower(root.get("name")),
                "%" + searchTerm.toLowerCase() + "%"
            );
        };
    }
}
