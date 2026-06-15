package com.sourashis.quizapp.modules.quiz.controller;

import com.sourashis.quizapp.core.audit.Auditable;
import com.sourashis.quizapp.core.response.ApiResponseWrapper;
import com.sourashis.quizapp.core.response.PaginationMeta;
import com.sourashis.quizapp.core.response.PageableUtil;
import com.sourashis.quizapp.modules.quiz.dto.CategoryRequest;
import com.sourashis.quizapp.modules.quiz.dto.CategoryResponse;
import com.sourashis.quizapp.modules.quiz.service.CategoryService;
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
import java.util.Set;

@Tag(name = "Categories", description = "Manage quiz categories (CRUD operations)")
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Operation(summary = "Get all categories", description = "Retrieves paginated list of quiz categories (public)")
    @ApiResponse(responseCode = "200", description = "Categories retrieved successfully")
    @Auditable(action = "READ", resourceType = "CATEGORY")
    @GetMapping
    public ResponseEntity<ApiResponseWrapper<List<CategoryResponse>>> getCategories(Pageable pageable) {
        Pageable safePageable = PageableUtil.safe(pageable, Set.of("name", "createdAt", "sortOrder"));
        Page<CategoryResponse> page = categoryService.getAllCategories(safePageable);
        return ApiResponseWrapper.paginated(
                page.getContent(),
                "Categories retrieved successfully",
                PaginationMeta.of(page));
    }

    @Operation(summary = "Create a category", description = "Creates a new quiz category")
    @ApiResponse(responseCode = "201", description = "Category created successfully")
    @Auditable(action = "CREATE", resourceType = "CATEGORY")
    @PostMapping
    @PreAuthorize("hasAuthority('category:create')")
    public ResponseEntity<ApiResponseWrapper<CategoryResponse>> addCategory(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.addCategory(request);
        return ApiResponseWrapper.created(response, "Category created successfully");
    }

    @Operation(summary = "Update a category", description = "Updates an existing category's details by ID")
    @ApiResponse(responseCode = "200", description = "Category updated successfully")
    @Auditable(action = "UPDATE", resourceType = "CATEGORY")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('category:update')")
    public ResponseEntity<ApiResponseWrapper<CategoryResponse>> editCategory(
            @PathVariable @Parameter(description = "ID of the category to update") Long id,
            @Valid @RequestBody CategoryRequest request) {
        CategoryResponse response = categoryService.editCategory(id, request);
        return ApiResponseWrapper.success(response, "Category updated successfully");
    }

    @Operation(summary = "Delete a category", description = "Deletes an existing category by its ID")
    @ApiResponse(responseCode = "200", description = "Category deleted successfully")
    @Auditable(action = "DELETE", resourceType = "CATEGORY")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('category:delete')")
    public ResponseEntity<ApiResponseWrapper<CategoryResponse>> deleteCategory(
            @PathVariable @Parameter(description = "ID of the category to delete") Long id) {
        CategoryResponse response = categoryService.deleteCategory(id);
        return ApiResponseWrapper.success(response, "Category deleted successfully");
    }
}
