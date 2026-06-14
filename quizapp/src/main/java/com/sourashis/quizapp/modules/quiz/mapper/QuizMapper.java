package com.sourashis.quizapp.modules.quiz.mapper;

import com.sourashis.quizapp.modules.question.entity.Question;
import com.sourashis.quizapp.modules.question.entity.QuestionOption;
import com.sourashis.quizapp.modules.quiz.dto.QuizQuestionResponse;
import com.sourashis.quizapp.modules.quiz.dto.QuizResponse;
import com.sourashis.quizapp.modules.quiz.dto.QuizScoreResponse;
import com.sourashis.quizapp.modules.quiz.entity.Quiz;
import com.sourashis.quizapp.modules.quiz.entity.QuizAnswer;
import com.sourashis.quizapp.modules.quiz.entity.QuizAttempt;
import com.sourashis.quizapp.modules.quiz.entity.QuizQuestion;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class QuizMapper {

    private QuizMapper() {}

    public static QuizQuestionResponse toQuestionResponse(Question q, List<QuestionOption> options) {
        List<QuizQuestionResponse.OptionResponse> optionResponses = options.stream()
                .map(opt -> QuizQuestionResponse.OptionResponse.builder()
                        .id(opt.getId())
                        .optionText(opt.getOptionText())
                        .sortOrder(opt.getSortOrder())
                        .build())
                .collect(Collectors.toList());

        return QuizQuestionResponse.builder()
                .id(q.getId())
                .questionTitle(q.getTitle())
                .options(optionResponses)
                .difficulty(q.getDifficulty().name())
                .points(q.getPoints())
                .build();
    }

    public static QuizResponse toResponse(Quiz q, List<QuizQuestion> qqs, Map<Long, Question> questionMap) {
        List<QuizQuestionResponse> questionResponses = qqs.stream()
                .sorted(java.util.Comparator.comparingInt(QuizQuestion::getSortOrder))
                .map(qq -> {
                    Question question = questionMap.get(qq.getQuestionId());
                    if (question == null) return null;
                    return toQuestionResponse(question, java.util.Collections.emptyList());
                })
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());

        return QuizResponse.builder()
                .id(q.getId())
                .uuid(q.getUuid())
                .title(q.getTitle())
                .description(q.getDescription())
                .categoryId(q.getCategory() != null ? q.getCategory().getId() : null)
                .categoryName(q.getCategory() != null ? q.getCategory().getName() : null)
                .difficulty(q.getDifficulty())
                .timeLimitMinutes(q.getTimeLimitMinutes())
                .totalQuestions(q.getTotalQuestions())
                .totalPoints(q.getTotalPoints())
                .questions(questionResponses)
                .build();
    }

    public static QuizResponse toResponse(Quiz q, List<QuizQuestion> qqs, Map<Long, Question> questionMap, Map<Long, List<QuestionOption>> optionsMap) {
        List<QuizQuestionResponse> questionResponses = qqs.stream()
                .sorted(java.util.Comparator.comparingInt(QuizQuestion::getSortOrder))
                .map(qq -> {
                    Question question = questionMap.get(qq.getQuestionId());
                    if (question == null) return null;
                    List<QuestionOption> options = optionsMap.getOrDefault(qq.getQuestionId(), java.util.Collections.emptyList());
                    return toQuestionResponse(question, options);
                })
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());

        return QuizResponse.builder()
                .id(q.getId())
                .uuid(q.getUuid())
                .title(q.getTitle())
                .description(q.getDescription())
                .categoryId(q.getCategory() != null ? q.getCategory().getId() : null)
                .categoryName(q.getCategory() != null ? q.getCategory().getName() : null)
                .difficulty(q.getDifficulty())
                .timeLimitMinutes(q.getTimeLimitMinutes())
                .totalQuestions(q.getTotalQuestions())
                .totalPoints(q.getTotalPoints())
                .questions(questionResponses)
                .build();
    }

    public static QuizScoreResponse toScoreResponse(QuizAttempt attempt, Quiz quiz, List<QuizAnswer> answers) {
        int totalQuestions = quiz.getTotalQuestions() != null ? quiz.getTotalQuestions() : answers.size();
        long correctCount = answers.stream().filter(a -> Boolean.TRUE.equals(a.getIsCorrect())).count();
        int wrongAnswers = totalQuestions - (int) correctCount;

        return QuizScoreResponse.builder()
                .quizId(quiz.getId())
                .quizTitle(quiz.getTitle())
                .attemptId(attempt.getId())
                .totalQuestions(totalQuestions)
                .correctAnswers((int) correctCount)
                .wrongAnswers(wrongAnswers)
                .scorePercentage(attempt.getScorePct() != null ? attempt.getScorePct() : 0.0)
                .passed(attempt.getPassed() != null && attempt.getPassed())
                .build();
    }
}
