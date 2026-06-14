package com.sourashis.quizapp.modules.quiz.service;

import com.sourashis.quizapp.modules.quiz.dto.CategoryRequest;
import com.sourashis.quizapp.modules.quiz.dto.CategoryResponse;
import com.sourashis.quizapp.modules.quiz.entity.Category;
import com.sourashis.quizapp.modules.quiz.exception.CategoryExistsException;
import com.sourashis.quizapp.modules.quiz.exception.CategoryNamesAreSameException;
import com.sourashis.quizapp.modules.quiz.exception.CategoryNotFoundException;
import com.sourashis.quizapp.modules.quiz.mapper.CategoryMapper;
import com.sourashis.quizapp.modules.quiz.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<CategoryResponse> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(CategoryMapper::toResponse);
    }

    @Transactional
    public CategoryResponse addCategory(CategoryRequest req) {
        if (categoryRepository.existsByName(req.getCategoryName())) {
            throw new CategoryExistsException(req.getCategoryName());
        }

        Category parent = null;
        if (req.getParentId() != null) {
            parent = categoryRepository.findById(req.getParentId())
                    .orElseThrow(() -> new CategoryNotFoundException(req.getParentId()));
        }

        Category category = Category.builder()
                .name(req.getCategoryName())
                .description(req.getDescription())
                .iconUrl(req.getIconUrl())
                .parent(parent)
                .build();
        category = categoryRepository.save(category);
        return CategoryMapper.toResponse(category);
    }

    @Transactional
    public CategoryResponse editCategory(Long id, CategoryRequest req) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        if (req.getCategoryName() != null) {
            if (req.getCategoryName().equals(category.getName())) {
                throw new CategoryNamesAreSameException(req.getCategoryName());
            }
            if (categoryRepository.existsByName(req.getCategoryName())) {
                throw new CategoryExistsException(req.getCategoryName());
            }
            category.setName(req.getCategoryName());
        }

        if (req.getDescription() != null) {
            category.setDescription(req.getDescription());
        }
        if (req.getIconUrl() != null) {
            category.setIconUrl(req.getIconUrl());
        }
        if (req.getParentId() != null) {
            Category parent = categoryRepository.findById(req.getParentId())
                    .orElseThrow(() -> new CategoryNotFoundException(req.getParentId()));
            category.setParent(parent);
        }

        category = categoryRepository.save(category);
        return CategoryMapper.toResponse(category);
    }

    @Transactional
    public CategoryResponse deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        category.setIsActive(false);
        category = categoryRepository.save(category);
        return CategoryMapper.toResponse(category);
    }
}
