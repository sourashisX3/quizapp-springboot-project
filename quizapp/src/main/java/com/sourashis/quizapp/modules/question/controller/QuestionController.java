package com.sourashis.quizapp.modules.question.controller;

import com.sourashis.quizapp.core.audit.Auditable;
import com.sourashis.quizapp.core.response.ApiResponseWrapper;
import com.sourashis.quizapp.core.response.PaginationMeta;
import com.sourashis.quizapp.modules.question.dto.QuestionListResponse;
import com.sourashis.quizapp.modules.question.dto.QuestionRequest;
import com.sourashis.quizapp.modules.question.dto.QuestionResponse;
import com.sourashis.quizapp.modules.question.service.QuestionService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Questions", description = "Question CRUD and management endpoints")
@RestController
@RequestMapping("/api/v1/questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Operation(summary = "Get all questions", description = "Retrieves a list of all questions in the system. This endpoint returns a simplified view without correct answer indicators. Use the paged version for large datasets. Requires the 'question:read' permission.")
    @ApiResponse(responseCode = "200", description = "Questions retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "403", description = "Forbidden — requires 'question:read' permission")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @Auditable(action = "READ", resourceType = "QUESTION")
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('question:read')")
    public ResponseEntity<ApiResponseWrapper<List<QuestionListResponse>>> getAllQuestions() {
        List<QuestionListResponse> questions = questionService.getAllQuestions();
        return ApiResponseWrapper.success(questions, "Questions retrieved successfully");
    }

    @Operation(summary = "Get paginated list of questions", description = "Retrieves a paginated list of all questions with sorting and filtering. Supports page, size, and sort query parameters. Requires the 'question:read' permission.")
    @ApiResponse(responseCode = "200", description = "Questions retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid pagination parameters")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "403", description = "Forbidden — requires 'question:read' permission")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @Auditable(action = "READ", resourceType = "QUESTION")
    @GetMapping("/all/paged")
    @PreAuthorize("hasAuthority('question:read')")
    public ResponseEntity<ApiResponseWrapper<List<QuestionListResponse>>> getAllQuestionsPaged(@Parameter(hidden = true) Pageable pageable) {
        Page<QuestionListResponse> page = questionService.getAllQuestionsPaged(pageable);
        return ApiResponseWrapper.paginated(
                page.getContent(),
                "Questions retrieved successfully",
                PaginationMeta.of(page));
    }

    @Operation(summary = "Get questions by category", description = "Retrieves all questions belonging to a specific category. Useful for populating a quiz creator's question pool filtered by topic. Requires the 'question:read' permission.")
    @ApiResponse(responseCode = "200", description = "Questions retrieved successfully")
    @ApiResponse(responseCode = "400", description = "Invalid category ID")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "403", description = "Forbidden — requires 'question:read' permission")
    @ApiResponse(responseCode = "404", description = "Category not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @Auditable(action = "READ", resourceType = "QUESTION")
    @GetMapping("/category/{categoryId}")
    @PreAuthorize("hasAuthority('question:read')")
    public ResponseEntity<ApiResponseWrapper<List<QuestionResponse>>> getQuestionsByCategory(@Parameter(description = "ID of the category to filter by", required = true) @PathVariable Long categoryId) {
        List<QuestionResponse> questions = questionService.getQuestionsByCategory(categoryId);
        return ApiResponseWrapper.success(questions, "Questions retrieved successfully");
    }

    @Operation(summary = "Get question by ID", description = "Retrieves the full details of a single question including all options. This endpoint reveals which option is correct. Requires the 'question:read' permission.")
    @ApiResponse(responseCode = "200", description = "Question retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "403", description = "Forbidden — requires 'question:read' permission")
    @ApiResponse(responseCode = "404", description = "Question not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @Auditable(action = "READ", resourceType = "QUESTION")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('question:read')")
    public ResponseEntity<ApiResponseWrapper<QuestionResponse>> getQuestionById(@Parameter(description = "ID of the question to retrieve", required = true) @PathVariable Long id) {
        QuestionResponse question = questionService.getQuestionById(id);
        return ApiResponseWrapper.success(question, "Question retrieved successfully");
    }

    @Operation(summary = "Create a new question", description = "Creates a new question with options. The request must include a title, categoryId, difficulty, questionType, and at least one option with a correct answer marked. At least one option must be marked as correct. Requires the 'question:create' permission.")
    @ApiResponse(responseCode = "201", description = "Question created successfully")
    @ApiResponse(responseCode = "400", description = "Validation failed — title is required, at least one option must be correct, options cannot be empty")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "403", description = "Forbidden — requires 'question:create' permission")
    @ApiResponse(responseCode = "404", description = "Referenced category not found")
    @ApiResponse(responseCode = "409", description = "Duplicate question title")
    @ApiResponse(responseCode = "422", description = "Invalid difficulty level or question type")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @Auditable(action = "CREATE", resourceType = "QUESTION")
    @PostMapping("/add")
    @PreAuthorize("hasAuthority('question:create')")
    public ResponseEntity<ApiResponseWrapper<QuestionResponse>> addQuestion(@Parameter(description = "Question creation payload") @Valid @RequestBody QuestionRequest request) {
        QuestionResponse question = questionService.addQuestion(request);
        return ApiResponseWrapper.created(question, "Question created successfully");
    }

    @Operation(summary = "Delete a question", description = "Permanently deletes a question by its ID. This action cannot be undone. Questions that are part of active quizzes or contests cannot be deleted. Requires the 'question:delete' permission.")
    @ApiResponse(responseCode = "200", description = "Question deleted successfully")
    @ApiResponse(responseCode = "401", description = "Authentication required")
    @ApiResponse(responseCode = "403", description = "Forbidden — requires 'question:delete' permission")
    @ApiResponse(responseCode = "404", description = "Question not found")
    @ApiResponse(responseCode = "409", description = "Question is referenced by active quizzes or contests and cannot be deleted")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @Auditable(action = "DELETE", resourceType = "QUESTION")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('question:delete')")
    public ResponseEntity<ApiResponseWrapper<Void>> deleteQuestion(@Parameter(description = "ID of the question to delete", required = true) @PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ApiResponseWrapper.success(null, "Question deleted successfully");
    }
}
