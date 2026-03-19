package com.sourashis.quizapp.modules.quiz.controller;

import com.sourashis.quizapp.core.response.ApiResponseWrapper;
import com.sourashis.quizapp.modules.quiz.dto.QuizRequest;
import com.sourashis.quizapp.modules.quiz.dto.QuizResponse;
import com.sourashis.quizapp.modules.quiz.dto.QuizScoreResponse;
import com.sourashis.quizapp.modules.quiz.dto.SubmitAnswerRequest;
import com.sourashis.quizapp.modules.quiz.service.QuizService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quiz")
public class QuizController {

    @Autowired
    private QuizService quizService;

    /**
     * POST /quiz/create
     * Creates a new quiz with random questions.
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponseWrapper<QuizResponse>> createQuiz(
            @Valid @RequestBody QuizRequest request) {
        QuizResponse response = quizService.createQuiz(request);
        return ApiResponseWrapper.created(response, "Quiz created successfully!");
    }

    /**
     * GET /quiz/{id}/questions
     * Returns the questions for a given quiz — rightAnswer excluded.
     */
    @GetMapping("/{id}/questions")
    public ResponseEntity<ApiResponseWrapper<QuizResponse>> getQuizQuestions(
            @PathVariable Integer id) {
        QuizResponse response = quizService.getQuizQuestions(id);
        return ApiResponseWrapper.success(response, "Quiz questions fetched successfully!");
    }

    /**
     * POST /quiz/{id}/submit
     * Submits answers and returns the score breakdown.
     */
    @PostMapping("/{id}/submit")
    public ResponseEntity<ApiResponseWrapper<QuizScoreResponse>> submitQuiz(
            @PathVariable Integer id,
            @Valid @RequestBody List<SubmitAnswerRequest> responses) {
        QuizScoreResponse score = quizService.calculateScore(id, responses);
        return ApiResponseWrapper.success(score, "Quiz submitted successfully!");
    }
}

