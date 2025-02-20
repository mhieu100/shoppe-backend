package com.project.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.project.dto.Pagination;

import com.project.exception.ExistException;
import com.project.exception.NotFoundException;
import com.project.model.Category;
import com.project.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Pagination getAllCategories(Specification<Category> specification, Pageable pageable) {
        Page<Category> pageCategory = categoryRepository.findAll(specification, pageable);
        Pagination pagination = new Pagination();
        Pagination.Meta meta = new Pagination.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());

        meta.setPages(pageCategory.getTotalPages());
        meta.setTotal(pageCategory.getTotalElements());

        pagination.setMeta(meta);

        List<Category> listCategorys = pageCategory.getContent().stream()
                .collect(Collectors.toList());

        pagination.setResult(listCategorys);

        return pagination;
    }

    public Category getCategoryById(Integer id) throws NotFoundException {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isEmpty()) {
            throw new NotFoundException("Không tìm thấy danh mục : " + id);
        }
        return category.get();
    }

    public Category createCategory(Category category) throws ExistException {
        Optional<Category> existcCategory = categoryRepository.findByName(category.getName());
        if (existcCategory.isPresent()) {
            throw new ExistException("Danh mục đã tồn tại : " + category.getName());
        }
        category.setCreateAt(new java.util.Date());
        Category savedCategory = categoryRepository.save(category);
        return savedCategory;
    }

    public Category updateCategory(Integer id, Category category) throws NotFoundException {
        Optional<Category> currentCategory = categoryRepository.findById(id);
        if (currentCategory.isEmpty()) {
            throw new NotFoundException("Không tìm thấy danh mục : " + id);
        }
        currentCategory.get().setName(category.getName());
        currentCategory.get().setDescription(category.getDescription());
        currentCategory.get().setUpdateAt(new java.util.Date());

        return categoryRepository.save(currentCategory.get());

    }

    public void deleteCategory(Integer id) throws NotFoundException {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isEmpty()) {
            throw new NotFoundException("Không tìm thấy danh mục : " + id);
        }
        categoryRepository.deleteById(id);
    }

}
