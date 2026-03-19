package com.sourashis.quizapp.modules.question.service;

import com.sourashis.quizapp.modules.question.dto.QuestionRequest;
import com.sourashis.quizapp.modules.question.dto.QuestionResponse;
import com.sourashis.quizapp.modules.question.entity.Question;
import com.sourashis.quizapp.modules.question.exception.QuestionNotFoundException;
import com.sourashis.quizapp.modules.question.mapper.QuestionMapper;
import com.sourashis.quizapp.modules.question.repository.QuestionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuestionService {

    private static final Logger log = LoggerFactory.getLogger(QuestionService.class);

    @Autowired
    private QuestionRepository questionRepository;

    // -- Get all questions (non-paginated) --
    public List<QuestionResponse> getAllQuestions() {
        log.info("Fetching all questions");
        return questionRepository.findAll()
                .stream()
                .map(QuestionMapper::toResponse)
                .toList();
    }

    // -- Get all questions (paginated) --
    public Page<QuestionResponse> getAllQuestions(Pageable pageable) {
        log.info("Fetching questions - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return questionRepository.findAll(pageable)
                .map(QuestionMapper::toResponse);
    }

    // -- Get questions by category --
    public List<QuestionResponse> getQuestionsByCategory(String category) {
        log.info("Fetching questions for category: {}", category);
        return questionRepository.findByCategory(category)
                .stream()
                .map(QuestionMapper::toResponse)
                .toList();
    }

    // -- Add question --
    @Transactional
    public QuestionResponse addQuestion(QuestionRequest request) {
        log.info("Adding new question in category: {}", request.getCategory());
        Question saved = questionRepository.save(QuestionMapper.toEntity(request));
        return QuestionMapper.toResponse(saved);
    }

    // -- Delete question --
    @Transactional
    public QuestionResponse deleteQuestionById(Integer id) {
        log.info("Deleting question with id: {}", id);
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new QuestionNotFoundException(id));
        questionRepository.deleteById(id);
        return QuestionMapper.toResponse(question);
    }
}


