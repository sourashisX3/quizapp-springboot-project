package com.sourashis.quizapp.modules.quiz.controller;

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

@RestController
@RequestMapping("/quiz")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private CategoryService categoryService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('quiz:create')")
    public ResponseEntity<ApiResponseWrapper<QuizResponse>> createQuiz(@Valid @RequestBody QuizRequest request) {
        QuizResponse response = quizService.createQuiz(request);
        return ApiResponseWrapper.created(response, "Quiz created successfully");
    }

    @GetMapping("/{id}/questions")
    @PreAuthorize("hasAuthority('quiz:read')")
    public ResponseEntity<ApiResponseWrapper<QuizResponse>> getQuizQuestions(@PathVariable Long id) {
        QuizResponse response = quizService.getQuizQuestions(id);
        return ApiResponseWrapper.success(response, "Quiz questions retrieved successfully");
    }

    @PostMapping("/{id}/submit")
    @PreAuthorize("hasAuthority('quiz:attempt')")
    public ResponseEntity<ApiResponseWrapper<QuizScoreResponse>> submitQuiz(
            @PathVariable Long id,
            @Valid @RequestBody List<SubmitAnswerRequest> responses) {
        QuizScoreResponse response = quizService.submitQuiz(id, responses);
        return ApiResponseWrapper.success(response, "Quiz submitted successfully");
    }

    @GetMapping("/categories")
    @PreAuthorize("hasAuthority('category:read')")
    public ResponseEntity<ApiResponseWrapper<List<CategoryResponse>>> getCategories(Pageable pageable) {
        Page<CategoryResponse> page = categoryService.getAllCategories(pageable);
        return ApiResponseWrapper.paginated(
                page.getContent(),
                "Categories retrieved successfully",
                PaginationMeta.of(page));
    }

    @PostMapping("/category/add")
    @PreAuthorize("hasAuthority('category:create')")
    public ResponseEntity<ApiResponseWrapper<CategoryResponse>> addCategory(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.addCategory(request);
        return ApiResponseWrapper.created(response, "Category created successfully");
    }

    @PutMapping("/category/edit/{id}")
    @PreAuthorize("hasAuthority('category:update')")
    public ResponseEntity<ApiResponseWrapper<CategoryResponse>> editCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.editCategory(id, request);
        return ApiResponseWrapper.success(response, "Category updated successfully");
    }

    @DeleteMapping("/category/delete/{id}")
    @PreAuthorize("hasAuthority('category:delete')")
    public ResponseEntity<ApiResponseWrapper<CategoryResponse>> deleteCategory(@PathVariable Long id) {
        CategoryResponse response = categoryService.deleteCategory(id);
        return ApiResponseWrapper.success(response, "Category deleted successfully");
    }
}
