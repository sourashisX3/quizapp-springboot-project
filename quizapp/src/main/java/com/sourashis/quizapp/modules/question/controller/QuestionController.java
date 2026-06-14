package com.sourashis.quizapp.modules.question.controller;

import com.sourashis.quizapp.core.response.ApiResponseWrapper;
import com.sourashis.quizapp.core.response.PaginationMeta;
import com.sourashis.quizapp.modules.question.dto.QuestionListResponse;
import com.sourashis.quizapp.modules.question.dto.QuestionRequest;
import com.sourashis.quizapp.modules.question.dto.QuestionResponse;
import com.sourashis.quizapp.modules.question.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('question:read')")
    public ResponseEntity<ApiResponseWrapper<List<QuestionListResponse>>> getAllQuestions() {
        List<QuestionListResponse> questions = questionService.getAllQuestions();
        return ApiResponseWrapper.success(questions, "Questions retrieved successfully");
    }

    @GetMapping("/all/paged")
    @PreAuthorize("hasAuthority('question:read')")
    public ResponseEntity<ApiResponseWrapper<List<QuestionListResponse>>> getAllQuestionsPaged(Pageable pageable) {
        Page<QuestionListResponse> page = questionService.getAllQuestionsPaged(pageable);
        return ApiResponseWrapper.paginated(
                page.getContent(),
                "Questions retrieved successfully",
                PaginationMeta.of(page));
    }

    @GetMapping("/category/{categoryId}")
    @PreAuthorize("hasAuthority('question:read')")
    public ResponseEntity<ApiResponseWrapper<List<QuestionResponse>>> getQuestionsByCategory(@PathVariable Long categoryId) {
        List<QuestionResponse> questions = questionService.getQuestionsByCategory(categoryId);
        return ApiResponseWrapper.success(questions, "Questions retrieved successfully");
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('question:read')")
    public ResponseEntity<ApiResponseWrapper<QuestionResponse>> getQuestionById(@PathVariable Long id) {
        QuestionResponse question = questionService.getQuestionById(id);
        return ApiResponseWrapper.success(question, "Question retrieved successfully");
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('question:create')")
    public ResponseEntity<ApiResponseWrapper<QuestionResponse>> addQuestion(@Valid @RequestBody QuestionRequest request) {
        QuestionResponse question = questionService.addQuestion(request);
        return ApiResponseWrapper.created(question, "Question created successfully");
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('question:delete')")
    public ResponseEntity<ApiResponseWrapper<Void>> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ApiResponseWrapper.success(null, "Question deleted successfully");
    }
}
