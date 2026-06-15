package com.sourashis.quizapp.modules.quiz.controller;

import com.sourashis.quizapp.core.audit.Auditable;
import com.sourashis.quizapp.core.response.ApiResponseWrapper;
import com.sourashis.quizapp.core.response.PaginationMeta;
import com.sourashis.quizapp.modules.quiz.dto.CategoryRequest;
import com.sourashis.quizapp.modules.quiz.dto.CategoryResponse;
import com.sourashis.quizapp.modules.quiz.dto.QuizRequest;
import com.sourashis.quizapp.modules.quiz.dto.QuizResponse;
import com.sourashis.quizapp.modules.quiz.dto.QuizScoreResponse;
import com.sourashis.quizapp.modules.quiz.dto.SubmitAnswerRequest;
import com.sourashis.quizapp.modules.quiz.service.CategoryService;
import com.sourashis.quizapp.modules.quiz.service.QuizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Quizzes", description = "Quiz creation, submission, and management endpoints")
@RestController
@RequestMapping("/api/v1/quiz")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private CategoryService categoryService;

    @Operation(summary = "Create a new quiz", description = "Creates a new quiz from a pool of questions. Requires a title, categoryId, difficulty level, time limit, passing score, and number of questions. Questions are randomly selected from the specified category. Requires the 'quiz:create' permission.")
    @ApiResponse(responseCode = "201", description = "Quiz created successfully")
    @ApiResponse(responseCode = "400", description = "Validation failed — title is required, numQuestions must be at least 1")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "403", description = "Forbidden — requires 'quiz:create' permission")
    @ApiResponse(responseCode = "404", description = "Referenced category not found")
    @ApiResponse(responseCode = "422", description = "Not enough questions in the specified category to create the quiz")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @Auditable(action = "CREATE", resourceType = "QUIZ")
    @PostMapping("/create")
    @PreAuthorize("hasAuthority('quiz:create')")
    public ResponseEntity<ApiResponseWrapper<QuizResponse>> createQuiz(@Valid @Parameter(description = "Quiz creation request containing title, categoryId, difficulty, timeLimit, passingScorePct, and numQuestions") @RequestBody QuizRequest request) {
        QuizResponse response = quizService.createQuiz(request);
        return ApiResponseWrapper.created(response, "Quiz created successfully");
    }

    @Operation(summary = "Get quiz questions", description = "Retrieves the questions and options for a specific quiz. This is the endpoint used to display questions to a user attempting the quiz. Does NOT reveal which options are correct. Requires the 'quiz:read' permission.")
    @ApiResponse(responseCode = "200", description = "Quiz questions retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "403", description = "Forbidden — requires 'quiz:read' permission")
    @ApiResponse(responseCode = "404", description = "Quiz not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @Auditable(action = "READ", resourceType = "QUIZ")
    @GetMapping("/{id}/questions")
    @PreAuthorize("hasAuthority('quiz:read')")
    public ResponseEntity<ApiResponseWrapper<QuizResponse>> getQuizQuestions(@PathVariable @Parameter(description = "ID of the quiz") Long id) {
        QuizResponse response = quizService.getQuizQuestions(id);
        return ApiResponseWrapper.success(response, "Quiz questions retrieved successfully");
    }

    @Operation(summary = "Submit quiz answers and get score", description = "Submits answers for a quiz attempt. Accepts a list of questionId + selectedOptionId pairs. The system evaluates each answer, calculates the score, and determines if the user passed based on the quiz's passingScorePct. This also triggers XP award, streak update, achievement checks, and gamification events. Respects maxAttempts limit. Requires the 'quiz:attempt' permission.")
    @ApiResponse(responseCode = "200", description = "Quiz submitted successfully. Returns score, correct/wrong counts, percentage, and pass status.")
    @ApiResponse(responseCode = "400", description = "Invalid request — mismatched question count or duplicate question IDs")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "403", description = "Forbidden — requires 'quiz:attempt' permission")
    @ApiResponse(responseCode = "404", description = "Quiz not found")
    @ApiResponse(responseCode = "409", description = "Maximum number of attempts exceeded for this quiz")
    @ApiResponse(responseCode = "422", description = "Quiz has expired or is no longer active")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @Auditable(action = "SUBMIT", resourceType = "QUIZ")
    @PostMapping("/{id}/submit")
    @PreAuthorize("hasAuthority('quiz:attempt')")
    public ResponseEntity<ApiResponseWrapper<QuizScoreResponse>> submitQuiz(
            @PathVariable @Parameter(description = "ID of the quiz to submit") Long id,
            @Valid @Parameter(description = "List of question answers") @RequestBody List<SubmitAnswerRequest> responses) {
        QuizScoreResponse response = quizService.submitQuiz(id, responses);
        return ApiResponseWrapper.success(response, "Quiz submitted successfully");
    }

    @Operation(summary = "Get quiz categories (legacy)", description = "Retrieves a paginated list of quiz categories using the legacy quiz endpoint. Prefer using the dedicated Category API at /api/v1/categories. Requires the 'category:read' permission.")
    @ApiResponse(responseCode = "200", description = "Categories retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "403", description = "Forbidden — requires 'category:read' permission")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @Auditable(action = "READ", resourceType = "CATEGORY")
    @GetMapping("/categories")
    @PreAuthorize("hasAuthority('category:read')")
    public ResponseEntity<ApiResponseWrapper<List<CategoryResponse>>> getCategories(@Parameter(hidden = true) Pageable pageable) {
        Page<CategoryResponse> page = categoryService.getAllCategories(pageable);
        return ApiResponseWrapper.paginated(
                page.getContent(),
                "Categories retrieved successfully",
                PaginationMeta.of(page));
    }

    @Operation(summary = "Add a new category (legacy)", description = "Creates a new quiz category. Accepts categoryName, description, and iconUrl. Prefer using the dedicated Category API at /api/v1/categories. Requires the 'category:create' permission.")
    @ApiResponse(responseCode = "201", description = "Category created successfully")
    @ApiResponse(responseCode = "400", description = "Validation failed — category name is required")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "403", description = "Forbidden — requires 'category:create' permission")
    @ApiResponse(responseCode = "409", description = "Category with this name already exists")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @Auditable(action = "CREATE", resourceType = "CATEGORY")
    @PostMapping("/category/add")
    @PreAuthorize("hasAuthority('category:create')")
    public ResponseEntity<ApiResponseWrapper<CategoryResponse>> addCategory(@Valid @Parameter(description = "Category details including categoryName, description, and iconUrl") @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.addCategory(request);
        return ApiResponseWrapper.created(response, "Category created successfully");
    }

    @Operation(summary = "Update a category (legacy)", description = "Updates an existing category's details. Only the fields provided in the request body will be updated. Prefer using the dedicated Category API at /api/v1/categories. Requires the 'category:update' permission.")
    @ApiResponse(responseCode = "200", description = "Category updated successfully")
    @ApiResponse(responseCode = "400", description = "Validation failed")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "403", description = "Forbidden — requires 'category:update' permission")
    @ApiResponse(responseCode = "404", description = "Category not found")
    @ApiResponse(responseCode = "409", description = "Category name conflicts with an existing category")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @Auditable(action = "UPDATE", resourceType = "CATEGORY")
    @PutMapping("/category/edit/{id}")
    @PreAuthorize("hasAuthority('category:update')")
    public ResponseEntity<ApiResponseWrapper<CategoryResponse>> editCategory(
            @PathVariable @Parameter(description = "ID of the category to edit") Long id,
            @Valid @Parameter(description = "Updated category details") @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.editCategory(id, request);
        return ApiResponseWrapper.success(response, "Category updated successfully");
    }

    @Operation(summary = "Delete a category (legacy)", description = "Deletes a quiz category by its ID. Categories that have associated quizzes or questions cannot be deleted. Prefer using the dedicated Category API at /api/v1/categories. Requires the 'category:delete' permission.")
    @ApiResponse(responseCode = "200", description = "Category deleted successfully")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "403", description = "Forbidden — requires 'category:delete' permission")
    @ApiResponse(responseCode = "404", description = "Category not found")
    @ApiResponse(responseCode = "409", description = "Category has associated quizzes or questions and cannot be deleted")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @Auditable(action = "DELETE", resourceType = "CATEGORY")
    @DeleteMapping("/category/delete/{id}")
    @PreAuthorize("hasAuthority('category:delete')")
    public ResponseEntity<ApiResponseWrapper<CategoryResponse>> deleteCategory(@PathVariable @Parameter(description = "ID of the category to delete") Long id) {
        CategoryResponse response = categoryService.deleteCategory(id);
        return ApiResponseWrapper.success(response, "Category deleted successfully");
    }
}
