package com.sourashis.quizapp.modules.question.service;

import com.sourashis.quizapp.modules.question.dto.QuestionRequest;
import com.sourashis.quizapp.modules.question.dto.QuestionResponse;
import com.sourashis.quizapp.modules.question.entity.Question;
import com.sourashis.quizapp.modules.question.exception.CategoryNotFoundException;
import com.sourashis.quizapp.modules.question.exception.QuestionNotFoundException;
import com.sourashis.quizapp.modules.question.mapper.QuestionMapper;
import com.sourashis.quizapp.modules.question.repository.QuestionRepository;
import com.sourashis.quizapp.modules.quiz.entity.Category;
import com.sourashis.quizapp.modules.quiz.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service layer for Question operations.
 * Handles business logic, validation, and coordination with repositories.
 * Properly separates concerns and maintains architectural boundaries.
 */
@Service
public class QuestionService {

    private static final Logger log = LoggerFactory.getLogger(QuestionService.class);

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * Get all questions (non-paginated).
     *
     * @return List of all questions as QuestionResponse DTOs
     */
    public List<QuestionResponse> getAllQuestions() {
        log.info("Fetching all questions");
        return questionRepository.findAll()
                .stream()
                .map(QuestionMapper::toResponse)
                .toList();
    }

    /**
     * Get all questions with pagination.
     *
     * @param pageable pagination parameters
     * @return Page of QuestionResponse DTOs
     */
    public Page<QuestionResponse> getAllQuestions(Pageable pageable) {
        log.info("Fetching questions - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return questionRepository.findAll(pageable)
                .map(QuestionMapper::toResponse);
    }

    /**
     * Get questions filtered by category name.
     * Validates that category exists before fetching questions.
     *
     * @param categoryName the category name to filter by
     * @return List of QuestionResponse DTOs for the given category
     * @throws CategoryNotFoundException if category doesn't exist
     */
    public List<QuestionResponse> getQuestionsByCategory(String categoryName) {
        log.info("Fetching questions for category: {}", categoryName);
        
        // Fetch category by name and validate existence
        Category category = categoryRepository.findByCategoryName(categoryName);
        if (category == null) {
            throw new CategoryNotFoundException(categoryName);
        }
        
        return questionRepository.findByCategory(category)
                .stream()
                .map(QuestionMapper::toResponse)
                .toList();
    }

    /**
     * Get questions filtered by category ID.
     * Validates that category exists before fetching questions.
     *
     * @param categoryId the category ID to filter by
     * @return List of QuestionResponse DTOs for the given category
     * @throws CategoryNotFoundException if category doesn't exist
     */
    public List<QuestionResponse> getQuestionsByCategoryId(Integer categoryId) {
        log.info("Fetching questions for category ID: {}", categoryId);
        
        // Fetch category by ID and validate existence
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
        
        return questionRepository.findByCategory(category)
                .stream()
                .map(QuestionMapper::toResponse)
                .toList();
    }

    /**
     * Add a new question.
     * Validates request data and that the referenced category exists.
     *
     * @param request the QuestionRequest DTO containing question details
     * @return the created QuestionResponse DTO
     * @throws CategoryNotFoundException if the category doesn't exist
     * @throws IllegalArgumentException if difficulty level is invalid
     */
    @Transactional
    public QuestionResponse addQuestion(QuestionRequest request) {
        log.info("Adding new question in category ID: {}", request.getCategoryId());
        
        // Mapper handles category validation and difficulty level conversion
        Question questionEntity = QuestionMapper.toEntity(request, categoryRepository);
        Question saved = questionRepository.save(questionEntity);
        
        log.info("Question added successfully with ID: {}", saved.getId());
        return QuestionMapper.toResponse(saved);
    }

    /**
     * Delete a question by ID.
     * Validates that the question exists before deletion.
     *
     * @param id the question ID to delete
     * @return the deleted QuestionResponse DTO
     * @throws QuestionNotFoundException if question doesn't exist
     */
    @Transactional
    public QuestionResponse deleteQuestionById(Integer id) {
        log.info("Deleting question with id: {}", id);
        
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new QuestionNotFoundException(id));
        
        questionRepository.deleteById(id);
        log.info("Question deleted successfully with ID: {}", id);
        
        return QuestionMapper.toResponse(question);
    }
}


