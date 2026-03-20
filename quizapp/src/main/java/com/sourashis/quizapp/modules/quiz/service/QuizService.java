package com.sourashis.quizapp.modules.quiz.service;

import com.sourashis.quizapp.modules.question.entity.Question;
import com.sourashis.quizapp.modules.question.repository.QuestionRepository;
import com.sourashis.quizapp.modules.quiz.dto.QuizRequest;
import com.sourashis.quizapp.modules.quiz.dto.QuizResponse;
import com.sourashis.quizapp.modules.quiz.dto.QuizScoreResponse;
import com.sourashis.quizapp.modules.quiz.dto.SubmitAnswerRequest;
import com.sourashis.quizapp.modules.quiz.entity.Category;
import com.sourashis.quizapp.modules.quiz.exception.CategoryNotFoundException;
import com.sourashis.quizapp.modules.quiz.exception.InsufficientQuestionsException;
import com.sourashis.quizapp.modules.quiz.entity.Quiz;
import com.sourashis.quizapp.modules.quiz.exception.QuizNotFoundException;
import com.sourashis.quizapp.modules.quiz.mapper.QuizMapper;
import com.sourashis.quizapp.modules.quiz.repository.CategoryRepository;
import com.sourashis.quizapp.modules.quiz.repository.QuizRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service layer for Quiz operations.
 * Handles business logic for quiz creation, retrieval, and scoring.
 * Validates all inputs and coordinates with repositories.
 */
@Service
public class QuizService {

    private static final Logger log = LoggerFactory.getLogger(QuizService.class);

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * Create a new quiz with random questions from the specified category.
     * Validates that:
     * - Category exists
     * - Enough questions are available in the category
     * - Number of questions is sufficient (minimum 5)
     *
     * @param request the quiz creation request containing title, categoryId, and numQuestions
     * @return QuizResponse with quiz details and randomly selected questions
     * @throws CategoryNotFoundException if category with given ID doesn't exist
     * @throws InsufficientQuestionsException if not enough questions available in category
     */
    @Transactional
    public QuizResponse createQuiz(QuizRequest request) {
        log.info("Creating quiz '{}' with {} questions from category ID: {}",
                request.getTitle(), request.getNumQuestions(), request.getCategoryId());

        // Step 1: Validate category exists
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> {
                    log.error("Category not found with ID: {}", request.getCategoryId());
                    return new CategoryNotFoundException(request.getCategoryId());
                });

        log.debug("Category found: {} (ID: {})", category.getCategoryName(), category.getId());

        // Step 2: Get random questions from the category
        List<Question> questions = questionRepository.findRandomQuestionsByCategory(
                request.getCategoryId(), request.getNumQuestions());

        // Step 3: Validate sufficient questions available
        if (questions.isEmpty()) {
            log.error("No questions available in category ID: {}", request.getCategoryId());
            throw new InsufficientQuestionsException(
                    request.getCategoryId(),
                    request.getNumQuestions(),
                    0
            );
        }

        if (questions.size() < request.getNumQuestions()) {
            log.warn("Insufficient questions in category ID: {}. Required: {}, Found: {}",
                    request.getCategoryId(), request.getNumQuestions(), questions.size());
            throw new InsufficientQuestionsException(
                    request.getCategoryId(),
                    request.getNumQuestions(),
                    questions.size()
            );
        }

        // Step 4: Create and save quiz
        Quiz quiz = new Quiz();
        quiz.setTitle(request.getTitle());
        quiz.setQuestions(questions);

        Quiz saved = quizRepository.save(quiz);
        log.info("Quiz created successfully - ID: {}, Title: {}, Questions: {}, Category: {}",
                saved.getId(), saved.getTitle(), questions.size(), category.getCategoryName());

        return QuizMapper.toResponse(saved);
    }

    /**
     * Retrieve quiz questions for a given quiz ID.
     * Returns questions without the correct answer to prevent cheating.
     *
     * @param id the quiz ID
     * @return QuizResponse with quiz details and questions
     * @throws QuizNotFoundException if quiz with given ID doesn't exist
     */
    public QuizResponse getQuizQuestions(Integer id) {
        log.info("Fetching questions for quiz ID: {}", id);
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Quiz not found with ID: {}", id);
                    return new QuizNotFoundException(id);
                });
        log.debug("Quiz found: {} with {} questions", quiz.getTitle(), quiz.getQuestions().size());
        return QuizMapper.toResponse(quiz);
    }

    /**
     * Calculate score for submitted quiz answers.
     * Compares user responses with correct answers and returns score breakdown.
     *
     * @param id the quiz ID
     * @param responses list of user's answers
     * @return QuizScoreResponse with score details including percentage
     * @throws QuizNotFoundException if quiz with given ID doesn't exist
     */
    public QuizScoreResponse calculateScore(Integer id, List<SubmitAnswerRequest> responses) {
        log.info("Calculating score for quiz ID: {} with {} responses", id, responses.size());
        
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Quiz not found with ID: {}", id);
                    return new QuizNotFoundException(id);
                });

        List<Question> questions = quiz.getQuestions();
        int correct = 0;

        // Compare each response with the correct answer
        for (int i = 0; i < responses.size(); i++) {
            SubmitAnswerRequest userResponse = responses.get(i);
            Question question = questions.get(i);
            
            if (userResponse.getResponse().equals(question.getRightAnswer())) {
                correct++;
                log.debug("Question ID {} - CORRECT", userResponse.getId());
            } else {
                log.debug("Question ID {} - WRONG (Expected: {}, Got: {})",
                        userResponse.getId(), question.getRightAnswer(), userResponse.getResponse());
            }
        }

        int total = questions.size();
        int wrong = total - correct;
        double percentage = total > 0 ? ((double) correct / total) * 100 : 0;

        log.info("Quiz {} - Score calculated: {} correct out of {} ({}%)",
                id, correct, total, Math.round(percentage));

        return QuizScoreResponse.builder()
                .quizId(quiz.getId())
                .quizTitle(quiz.getTitle())
                .totalQuestions(total)
                .correctAnswers(correct)
                .wrongAnswers(wrong)
                .scorePercentage(Math.round(percentage * 100.0) / 100.0)
                .build();
    }
}

