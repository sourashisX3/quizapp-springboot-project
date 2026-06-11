package com.sourashis.quizapp.modules.question.controller;

import com.sourashis.quizapp.core.response.ApiResponseWrapper;
import com.sourashis.quizapp.core.response.PaginationMeta;
import com.sourashis.quizapp.modules.question.dto.QuestionRequest;
import com.sourashis.quizapp.modules.question.dto.QuestionResponse;
import com.sourashis.quizapp.modules.question.service.QuestionService;
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
 * REST Controller for Question operations.
 * Handles HTTP requests for question CRUD operations.
 * Provides both paginated and non-paginated endpoints.
 */
@RestController
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    /**
     * GET /question/all
     * Returns all questions without pagination.
     * Access: Any authenticated user with 'question:read' permission
     *
     * @return ResponseEntity with list of all questions
     */
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('question:read')")
    public ResponseEntity<ApiResponseWrapper<List<QuestionResponse>>> getAllQuestions() {
        List<QuestionResponse> questions = questionService.getAllQuestions();
        return ApiResponseWrapper.success(questions, "Questions fetched successfully!");
    }

    /**
     * GET /question/all/paged?page=0&size=10&sortBy=id
     * Returns paginated questions with metadata.
     * Access: Any authenticated user with 'question:read' permission
     *
     * @param page   zero-indexed page number (default: 0)
     * @param size   page size (default: 10)
     * @param sortBy field to sort by (default: id)
     * @return ResponseEntity with paginated questions and pagination metadata
     */
    @GetMapping("/all/paged")
    @PreAuthorize("hasAuthority('question:read')")
    public ResponseEntity<ApiResponseWrapper<List<QuestionResponse>>> getAllQuestionsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        Page<QuestionResponse> result = questionService.getAllQuestions(
                PageRequest.of(page, size, Sort.by(sortBy)));
        return ApiResponseWrapper.paginated(
                result.getContent(),
                "Questions fetched successfully!",
                PaginationMeta.of(result));
    }

    /**
     * GET /question/category/{categoryName}
     * Returns questions filtered by category name.
     * Access: Any authenticated user with 'question:read' permission
     *
     * @param categoryName the name of the category to filter by
     * @return ResponseEntity with list of questions for the given category
     * @throws com.sourashis.quizapp.modules.question.exception.CategoryNotFoundException if category doesn't exist
     */
    @GetMapping("/category/{categoryName}")
    @PreAuthorize("hasAuthority('question:read')")
    public ResponseEntity<ApiResponseWrapper<List<QuestionResponse>>> getQuestionsByCategory(
            @PathVariable String categoryName) {
        List<QuestionResponse> questions = questionService.getQuestionsByCategory(categoryName);
        return ApiResponseWrapper.success(questions, "Questions fetched successfully!");
    }

    /**
     * GET /question/category-id/{categoryId}
     * Returns questions filtered by category ID.
     * Access: Any authenticated user with 'question:read' permission
     *
     * @param categoryId the ID of the category to filter by
     * @return ResponseEntity with list of questions for the given category
     * @throws com.sourashis.quizapp.modules.question.exception.CategoryNotFoundException if category doesn't exist
     */
    @GetMapping("/category-id/{categoryId}")
    @PreAuthorize("hasAuthority('question:read')")
    public ResponseEntity<ApiResponseWrapper<List<QuestionResponse>>> getQuestionsByCategoryId(
            @PathVariable Integer categoryId) {
        List<QuestionResponse> questions = questionService.getQuestionsByCategoryId(categoryId);
        return ApiResponseWrapper.success(questions, "Questions fetched successfully!");
    }

    /**
     * POST /question/add
     * Creates a new question.
     * Validates that the referenced category exists and difficulty level is valid.
     * Access: Any authenticated user with 'question:create' permission
     *
     * @param request the QuestionRequest DTO with question details
     * @return ResponseEntity with created question
     * @throws com.sourashis.quizapp.modules.question.exception.CategoryNotFoundException if referenced category doesn't exist
     * @throws IllegalArgumentException if difficulty level is invalid
     */
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('question:create')")
    public ResponseEntity<ApiResponseWrapper<QuestionResponse>> addQuestion(
            @Valid @RequestBody QuestionRequest request) {
        QuestionResponse response = questionService.addQuestion(request);
        return ApiResponseWrapper.created(response, "Question added successfully!");
    }

    /**
     * DELETE /question/delete/{id}
     * Deletes a question by its ID.
     * Access: Any authenticated user with 'question:delete' permission
     *
     * @param id the ID of the question to delete
     * @return ResponseEntity with deleted question details
     * @throws com.sourashis.quizapp.modules.question.exception.QuestionNotFoundException if question doesn't exist
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('question:delete')")
    public ResponseEntity<ApiResponseWrapper<QuestionResponse>> deleteQuestion(@PathVariable Integer id) {
        QuestionResponse response = questionService.deleteQuestionById(id);
        return ApiResponseWrapper.success(response, "Question deleted successfully!");
    }
}
