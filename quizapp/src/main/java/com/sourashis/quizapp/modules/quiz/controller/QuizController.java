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
     * Access: ADMIN only
     *
     * @param request QuizRequest with title, categoryId, and numQuestions
     * @return ResponseEntity with created quiz and randomly selected questions
     * @throws CategoryNotFoundException      if category doesn't exist
     * @throws InsufficientQuestionsException if not enough questions in category
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseWrapper<QuizResponse>> createQuiz(
            @Valid @RequestBody QuizRequest request) {
        QuizResponse response = quizService.createQuiz(request);
        return ApiResponseWrapper.created(response, "Quiz created successfully!");
    }

    /**
     * GET /quiz/{id}/questions
     * Returns the questions for a given quiz — rightAnswer excluded.
     * Access: Any authenticated user (ADMIN or USER)
     */
    @GetMapping("/{id}/questions")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponseWrapper<QuizResponse>> getQuizQuestions(
            @PathVariable Integer id) {
        QuizResponse response = quizService.getQuizQuestions(id);
        return ApiResponseWrapper.success(response, "Quiz questions fetched successfully!");
    }

    /**
     * POST /quiz/{id}/submit
     * Submits answers and returns the score breakdown.
     * Access: Any authenticated user (ADMIN or USER)
     */
    @PostMapping("/{id}/submit")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<ApiResponseWrapper<QuizScoreResponse>> submitQuiz(
            @PathVariable Integer id,
            @Valid @RequestBody List<SubmitAnswerRequest> responses) {
        QuizScoreResponse score = quizService.calculateScore(id, responses);
        return ApiResponseWrapper.success(score, "Quiz submitted successfully!");
    }


    /**
     * GET /quiz/categories/paged?page=0&size=10&sortBy=id
     * Returns all categories.
     * Access: Any authenticated user (ADMIN or USER)
     */
    @GetMapping("/categories")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
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
     * Access: ADMIN only
     */
    @PostMapping("/category/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseWrapper<CategoryResponse>> addCategory(
            @Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.addCategory(request);
        return ApiResponseWrapper.created(response, "Category added successfully!");
    }


    /**
     * PUT /quiz/category/edit/{id}
     * Edits a category.
     * Access: ADMIN only
     */
    @PutMapping("/category/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseWrapper<CategoryResponse>> editCategory(
            @PathVariable Integer id,
            @Valid @RequestBody CategoryRequest request
    ) {
        CategoryResponse response = categoryService.editCategory(id, request);
        return ApiResponseWrapper.success(response, "Category edited successfully!");
    }

    /**
     * DELETE /quiz/category/delete/{id}
     * Deletes a category.
     * Access: ADMIN only
     */
    @DeleteMapping("/category/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseWrapper<CategoryResponse>> deleteCategory(@PathVariable Integer id) {
        CategoryResponse response = categoryService.deleteCategory(id);
        return ApiResponseWrapper.success(response, "Category deleted successfully!");
    }
}
