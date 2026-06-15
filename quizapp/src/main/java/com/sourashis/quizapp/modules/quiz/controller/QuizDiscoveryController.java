package com.sourashis.quizapp.modules.quiz.controller;

import com.sourashis.quizapp.core.response.ApiResponseWrapper;
import com.sourashis.quizapp.core.response.PaginationMeta;
import com.sourashis.quizapp.modules.auth.entity.User;
import com.sourashis.quizapp.modules.quiz.dto.QuizSummaryResponse;
import com.sourashis.quizapp.modules.quiz.service.QuizDiscoveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Tag(name = "Quiz Discovery", description = "Browse, search, and discover quizzes")
@RestController
@RequestMapping("/api/v1/quizzes")
public class QuizDiscoveryController {

    @Autowired
    private QuizDiscoveryService quizDiscoveryService;

    @Operation(summary = "Get recent quizzes", description = "Retrieves the most recently created published quizzes (public)")
    @ApiResponse(responseCode = "200", description = "Recent quizzes retrieved successfully")
    @GetMapping("/recent")
    public ResponseEntity<ApiResponseWrapper<List<QuizSummaryResponse>>> getRecentQuizzes(
            @RequestParam(defaultValue = "10") @Parameter(description = "Number of recent quizzes to return") int limit) {
        List<QuizSummaryResponse> quizzes = quizDiscoveryService.getRecentQuizzes(limit);
        return ApiResponseWrapper.success(quizzes, "Recent quizzes retrieved successfully");
    }

    @Operation(summary = "Get quizzes by category", description = "Retrieves paginated quizzes for a specific category (public)")
    @ApiResponse(responseCode = "200", description = "Quizzes retrieved successfully")
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponseWrapper<List<QuizSummaryResponse>>> getQuizzesByCategory(
            @PathVariable @Parameter(description = "ID of the category") Long categoryId,
            @RequestParam(defaultValue = "0") @Parameter(description = "Page number") int page,
            @RequestParam(defaultValue = "20") @Parameter(description = "Page size") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<QuizSummaryResponse> quizPage = quizDiscoveryService.getQuizzesByCategory(categoryId, pageable);
        return ApiResponseWrapper.paginated(quizPage.getContent(), "Quizzes retrieved successfully",
                PaginationMeta.of(quizPage));
    }

    @Operation(summary = "Get trending categories", description = "Retrieves categories with the most quiz attempts (public)")
    @ApiResponse(responseCode = "200", description = "Trending categories retrieved successfully")
    @GetMapping("/trending-categories")
    public ResponseEntity<ApiResponseWrapper<List<Map<String, Object>>>> getTrendingCategories() {
        List<Map<String, Object>> categories = quizDiscoveryService.getTrendingCategories();
        return ApiResponseWrapper.success(categories, "Trending categories retrieved successfully");
    }

    @Operation(summary = "Search quizzes", description = "Searches quizzes by title with pagination (public)")
    @ApiResponse(responseCode = "200", description = "Search results retrieved successfully")
    @GetMapping("/search")
    public ResponseEntity<ApiResponseWrapper<List<QuizSummaryResponse>>> searchQuizzes(
            @RequestParam("q") @Parameter(description = "Search query for quiz title") String query,
            @RequestParam(defaultValue = "0") @Parameter(description = "Page number") int page,
            @RequestParam(defaultValue = "20") @Parameter(description = "Page size") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<QuizSummaryResponse> quizPage = quizDiscoveryService.searchQuizzes(query, pageable);
        return ApiResponseWrapper.paginated(quizPage.getContent(), "Search results retrieved successfully",
                PaginationMeta.of(quizPage));
    }

    @Operation(summary = "Get recommended quizzes", description = "Retrieves recommended quizzes based on user's most attempted category")
    @ApiResponse(responseCode = "200", description = "Recommended quizzes retrieved successfully")
    @GetMapping("/recommended")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponseWrapper<List<QuizSummaryResponse>>> getRecommendedQuizzes() {
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<QuizSummaryResponse> quizzes = quizDiscoveryService.getRecommendedQuizzes(currentUser.getId());
        return ApiResponseWrapper.success(quizzes, "Recommended quizzes retrieved successfully");
    }
}
