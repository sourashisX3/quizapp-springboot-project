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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/question")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    /**
     * GET /question/all
     * Returns all questions — no meta in response.
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponseWrapper<List<QuestionResponse>>> getAllQuestions() {
        List<QuestionResponse> questions = questionService.getAllQuestions();
        return ApiResponseWrapper.success(questions, "Questions fetched successfully!");
    }

    /**
     * GET /question/all/paged?page=0&size=10&sortBy=id
     * Returns paginated questions — meta included in response.
     */
    @GetMapping("/all/paged")
    public ResponseEntity<ApiResponseWrapper<List<QuestionResponse>>> getAllQuestionsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {
        Page<QuestionResponse> result = questionService.getAllQuestions(PageRequest.of(page, size, Sort.by(sortBy)));
        return ApiResponseWrapper.paginated(result.getContent(), "Questions fetched successfully!", PaginationMeta.of(result));
    }

    /**
     * GET /question/category/{categoryName}
     * Returns questions filtered by category — no meta.
     */
    @GetMapping("/category/{categoryName}")
    public ResponseEntity<ApiResponseWrapper<List<QuestionResponse>>> getQuestionsByCategory(
            @PathVariable String categoryName) {
        List<QuestionResponse> questions = questionService.getQuestionsByCategory(categoryName);
        return ApiResponseWrapper.success(questions, "Questions fetched successfully!");
    }

    /**
     * POST /question/add
     * Adds a new question.
     */
    @PostMapping("/add")
    public ResponseEntity<ApiResponseWrapper<QuestionResponse>> addQuestion(
            @Valid @RequestBody QuestionRequest request) {
        QuestionResponse response = questionService.addQuestion(request);
        return ApiResponseWrapper.created(response, "Question added successfully!");
    }

    /**
     * DELETE /question/delete/{id}
     * Deletes a question by id.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponseWrapper<QuestionResponse>> deleteQuestion(@PathVariable Integer id) {
        QuestionResponse response = questionService.deleteQuestionById(id);
        return ApiResponseWrapper.success(response, "Question deleted successfully!");
    }
}

