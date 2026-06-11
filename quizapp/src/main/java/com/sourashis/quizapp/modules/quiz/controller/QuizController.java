package com.sourashis.quizapp.modules.quiz.controller;

import com.sourashis.quizapp.core.response.ApiResponseWrapper;
import com.sourashis.quizapp.core.response.PaginationMeta;
import com.sourashis.quizapp.modules.quiz.dto.*;
import com.sourashis.quizapp.modules.quiz.service.CategoryService;
import com.sourashis.quizapp.modules.quiz.service.QuizService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Quiz operations.
 * Handles quiz creation, retrieval, scoring, and category management.
 */
@RestController
@RequestMapping("/quiz")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private CategoryService categoryService;

    /**
     * POST /quiz/create
     * Creates a new quiz with random questions from the specified category.
     * Validates category exists and sufficient questions are available.
     * Access: Any authenticated user with 'quiz:create' permission
     *
     * @param request QuizRequest with title, categoryId, and numQuestions
     * @return ResponseEntity with created quiz and randomly selected questions
     * @throws com.sourashis.quizapp.modules.quiz.exception.CategoryNotFoundException if category doesn't exist
     * @throws com.sourashis.quizapp.modules.quiz.exception.InsufficientQuestionsException if not enough questions in category
     */
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('quiz:create')")
    public ResponseEntity<ApiResponseWrapper<QuizResponse>> createQuiz(
            @Valid @RequestBody QuizRequest request) {
        QuizResponse response = quizService.createQuiz(request);
        return ApiResponseWrapper.created(response, "Quiz created successfully!");
    }

    /**
     * GET /quiz/{id}/questions
     * Returns the questions for a given quiz — rightAnswer excluded.
     * Access: Any authenticated user with 'quiz:read' permission
     *
     * @param id the quiz ID
     * @return ResponseEntity with quiz details and questions
     * @throws com.sourashis.quizapp.modules.quiz.exception.QuizNotFoundException if quiz doesn't exist
     */
    @GetMapping("/{id}/questions")
    @PreAuthorize("hasAuthority('quiz:read')")
    public ResponseEntity<ApiResponseWrapper<QuizResponse>> getQuizQuestions(
            @PathVariable Integer id) {
        QuizResponse response = quizService.getQuizQuestions(id);
        return ApiResponseWrapper.success(response, "Quiz questions fetched successfully!");
    }

    /**
     * POST /quiz/{id}/submit
     * Submits answers and returns the score breakdown.
     * Access: Any authenticated user with 'quiz:attempt' permission
     *
     * @param id        the quiz ID
     * @param responses list of user's answers
     * @return ResponseEntity with score details including percentage
     * @throws com.sourashis.quizapp.modules.quiz.exception.QuizNotFoundException if quiz doesn't exist
     */
    @PostMapping("/{id}/submit")
    @PreAuthorize("hasAuthority('quiz:attempt')")
    public ResponseEntity<ApiResponseWrapper<QuizScoreResponse>> submitQuiz(
            @PathVariable Integer id,
            @Valid @RequestBody List<SubmitAnswerRequest> responses) {
        QuizScoreResponse score = quizService.calculateScore(id, responses);
        return ApiResponseWrapper.success(score, "Quiz submitted successfully!");
    }

    /**
     * GET /quiz/categories?page=0&size=10&sortBy=id
     * Returns all categories with pagination.
     * Access: Any authenticated user with 'category:read' permission
     *
     * @param page   zero-indexed page number (default: 0)
     * @param size   page size (default: 10)
     * @param sortBy field to sort by (default: id)
     * @return ResponseEntity with paginated list of categories
     */
    @GetMapping("/categories")
    @PreAuthorize("hasAuthority('category:read')")
    public ResponseEntity<ApiResponseWrapper<List<CategoryResponse>>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy
    ) {
        Page<CategoryResponse> response = categoryService.getAllCategories(
                PageRequest.of(page, size, Sort.by(sortBy))
        );
        return ApiResponseWrapper.paginated(
                response.getContent(),
                "Categories fetched successfully!",
                PaginationMeta.of(response)
        );
    }

    /**
     * POST /quiz/category/add
     * Adds a new category.
     * Access: Any authenticated user with 'category:create' permission
     *
     * @param request CategoryRequest with category name
     * @return ResponseEntity with created category
     * @throws com.sourashis.quizapp.modules.quiz.exception.CategoryExistsException if category already exists
     */
    @PostMapping("/category/add")
    @PreAuthorize("hasAuthority('category:create')")
    public ResponseEntity<ApiResponseWrapper<CategoryResponse>> addCategory(
            @Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.addCategory(request);
        return ApiResponseWrapper.created(response, "Category added successfully!");
    }

    /**
     * PUT /quiz/category/edit/{id}
     * Edits an existing category name.
     * Access: Any authenticated user with 'category:update' permission
     *
     * @param id      the category ID to edit
     * @param request CategoryRequest with new category name
     * @return ResponseEntity with updated category
     * @throws com.sourashis.quizapp.modules.quiz.exception.CategoryNotFoundException if category doesn't exist
     * @throws com.sourashis.quizapp.modules.quiz.exception.CategoryNamesAreSameException if new name matches existing
     */
    @PutMapping("/category/edit/{id}")
    @PreAuthorize("hasAuthority('category:update')")
    public ResponseEntity<ApiResponseWrapper<CategoryResponse>> editCategory(
            @PathVariable Integer id,
            @Valid @RequestBody CategoryRequest request
    ) {
        CategoryResponse response = categoryService.editCategory(id, request);
        return ApiResponseWrapper.success(response, "Category edited successfully!");
    }

    /**
     * DELETE /quiz/category/delete/{id}
     * Deletes a category by its ID.
     * Access: Any authenticated user with 'category:delete' permission
     *
     * @param id the category ID to delete
     * @return ResponseEntity with deleted category details
     * @throws com.sourashis.quizapp.modules.quiz.exception.CategoryNotFoundException if category doesn't exist
     */
    @DeleteMapping("/category/delete/{id}")
    @PreAuthorize("hasAuthority('category:delete')")
    public ResponseEntity<ApiResponseWrapper<CategoryResponse>> deleteCategory(@PathVariable Integer id) {
        CategoryResponse response = categoryService.deleteCategory(id);
        return ApiResponseWrapper.success(response, "Category deleted successfully!");
    }
}
