package com.sourashis.quizapp.modules.question.service;

import com.sourashis.quizapp.modules.question.dto.QuestionListResponse;
import com.sourashis.quizapp.modules.question.dto.QuestionRequest;
import com.sourashis.quizapp.modules.question.dto.QuestionResponse;
import com.sourashis.quizapp.modules.question.entity.Question;
import com.sourashis.quizapp.modules.question.entity.QuestionOption;
import com.sourashis.quizapp.modules.question.exception.CategoryNotFoundException;
import com.sourashis.quizapp.modules.question.exception.QuestionNotFoundException;
import com.sourashis.quizapp.modules.question.mapper.QuestionMapper;
import com.sourashis.quizapp.modules.question.repository.QuestionOptionRepository;
import com.sourashis.quizapp.modules.question.repository.QuestionRepository;
import com.sourashis.quizapp.modules.quiz.entity.Category;
import com.sourashis.quizapp.modules.quiz.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Transactional
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionOptionRepository questionOptionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<QuestionListResponse> getAllQuestions() {
        return questionRepository.findByIsActiveTrue(Pageable.unpaged()).getContent()
                .stream()
                .map(QuestionMapper::toListResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<QuestionListResponse> getAllQuestionsPaged(Pageable pageable) {
        return questionRepository.findByIsActiveTrue(pageable)
                .map(QuestionMapper::toListResponse);
    }

    @Transactional(readOnly = true)
    public List<QuestionResponse> getQuestionsByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
        return questionRepository.findByCategoryId(categoryId)
                .stream()
                .map(QuestionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public QuestionResponse getQuestionById(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new QuestionNotFoundException(id));
        return QuestionMapper.toResponse(question);
    }

    public QuestionResponse addQuestion(QuestionRequest req) {
        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(req.getCategoryId()));

        Question question = QuestionMapper.toEntity(req, category);
        final Question savedQuestion = questionRepository.save(question);

        List<QuestionOption> options = IntStream.range(0, req.getOptions().size())
                .mapToObj(i -> {
                    QuestionRequest.OptionRequest opt = req.getOptions().get(i);
                    return QuestionOption.builder()
                            .question(savedQuestion)
                            .optionText(opt.getOptionText())
                            .isCorrect(opt.isCorrect())
                            .sortOrder(i)
                            .explanation(opt.getExplanation())
                            .build();
                })
                .collect(Collectors.toList());
        questionOptionRepository.saveAll(options);

        return QuestionMapper.toResponse(savedQuestion);
    }

    public void deleteQuestion(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new QuestionNotFoundException(id));
        question.setActive(false);
        questionRepository.save(question);
    }
}
