package com.licorerajm.backend.service;

import com.licorerajm.backend.dto.CategoryRequest;
import com.licorerajm.backend.dto.CategoryResponse;
import com.licorerajm.backend.entity.Category;
import com.licorerajm.backend.exception.ResourceNotFoundException;
import com.licorerajm.backend.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CategoryResponse findById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("La categoría no fue encontrada"));

        return toResponse(category);
    }

    public CategoryResponse create(CategoryRequest request) {
        Category category = new Category();

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setActive(true);

        Category savedCategory = categoryRepository.save(category);

        return toResponse(savedCategory);
    }

    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("La categoría no fue encontrada"));

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        Category updatedCategory = categoryRepository.save(category);

        return toResponse(updatedCategory);
    }

    public void deactivate(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("La categoría no fue encontrada"));

        category.setActive(false);

        categoryRepository.save(category);
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getActive(),
                category.getCreatedAt()
        );
    }
}