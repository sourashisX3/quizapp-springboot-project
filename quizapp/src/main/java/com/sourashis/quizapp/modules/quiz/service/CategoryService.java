package com.sourashis.quizapp.modules.quiz.service;

import com.sourashis.quizapp.modules.quiz.dto.CategoryRequest;
import com.sourashis.quizapp.modules.quiz.dto.CategoryResponse;
import com.sourashis.quizapp.modules.quiz.entity.Category;
import com.sourashis.quizapp.modules.quiz.exception.CategoryExistsException;
import com.sourashis.quizapp.modules.quiz.exception.CategoryNamesAreSameException;
import com.sourashis.quizapp.modules.quiz.exception.CategoryNotFoundException;
import com.sourashis.quizapp.modules.quiz.mapper.CategoryMapper;
import com.sourashis.quizapp.modules.quiz.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private static final Logger log = LoggerFactory.getLogger(CategoryService.class);

    @Autowired
    private CategoryRepository categoryRepository;

    // add category
    public CategoryResponse addCategory(CategoryRequest request) {

        // check if category already exists
        boolean isCategoryExists = categoryRepository.findByCategoryName(request.getCategoryName()) != null;
        if (isCategoryExists) {
            log.warn("Category already exists: {}", request.getCategoryName());
            throw new CategoryExistsException(request.getCategoryName());
        }

        Category category = new Category();
        category.setCategoryName(request.getCategoryName());
        Category saved = categoryRepository.save(category);

        log.info("Category added: {}", saved);
        return CategoryMapper.toCategoryResponse(saved);
    }

    // edit category
    public CategoryResponse editCategory(Integer id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        // check category names are same or not
        boolean isCategoriesSame = category.getCategoryName().equals(request.getCategoryName());
        if (isCategoriesSame) {
            log.warn("Category name is same as existing: {}", request.getCategoryName());
            throw new CategoryNamesAreSameException(request.getCategoryName());
        }
        category.setCategoryName(request.getCategoryName());
        Category saved = categoryRepository.save(category);

        log.info("Category edited: {}", saved);
        return CategoryMapper.toCategoryResponse(saved);
    }

    // delete category
    public CategoryResponse deleteCategory(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));
        categoryRepository.delete(category);

        log.info("Category deleted: {}", category);
        return CategoryMapper.toCategoryResponse(category);
    }

    /**
     * Get all categories.
     *
     * @param pageable pagination parameters (optional)
     * @return List of all categories as CategoryResponse DTOs
     * @throws CategoryNotFoundException if no categories exist
     */
    public Page<CategoryResponse> getAllCategories(Pageable pageable) {
        List<Category> categories = categoryRepository.findAll();
        log.info("Fetched all categories - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return categoryRepository.findAll(pageable)
                .map(CategoryMapper::toCategoryResponse);
    }
}
