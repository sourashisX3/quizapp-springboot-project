package com.sourashis.quizapp.modules.quiz.service;

import com.sourashis.quizapp.modules.analytics.entity.UserStatistics;
import com.sourashis.quizapp.modules.analytics.repository.UserStatisticsRepository;
import com.sourashis.quizapp.modules.auth.entity.User;
import com.sourashis.quizapp.modules.question.entity.Question;
import com.sourashis.quizapp.modules.question.entity.QuestionOption;
import com.sourashis.quizapp.modules.question.repository.QuestionOptionRepository;
import com.sourashis.quizapp.modules.question.repository.QuestionRepository;
import com.sourashis.quizapp.modules.quiz.dto.QuizRequest;
import com.sourashis.quizapp.modules.quiz.dto.QuizResponse;
import com.sourashis.quizapp.modules.quiz.dto.QuizScoreResponse;
import com.sourashis.quizapp.modules.quiz.dto.SubmitAnswerRequest;
import com.sourashis.quizapp.modules.quiz.entity.Category;
import com.sourashis.quizapp.modules.quiz.entity.Quiz;
import com.sourashis.quizapp.modules.quiz.entity.QuizAnswer;
import com.sourashis.quizapp.modules.quiz.entity.QuizAttempt;
import com.sourashis.quizapp.modules.quiz.entity.QuizQuestion;
import com.sourashis.quizapp.modules.quiz.exception.CategoryNotFoundException;
import com.sourashis.quizapp.modules.quiz.exception.InsufficientQuestionsException;
import com.sourashis.quizapp.modules.quiz.exception.QuizNotFoundException;
import com.sourashis.quizapp.modules.quiz.mapper.QuizMapper;
import com.sourashis.quizapp.modules.quiz.repository.CategoryRepository;
import com.sourashis.quizapp.modules.quiz.repository.QuizAnswerRepository;
import com.sourashis.quizapp.modules.quiz.repository.QuizAttemptRepository;
import com.sourashis.quizapp.modules.quiz.repository.QuizQuestionRepository;
import com.sourashis.quizapp.modules.quiz.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @Autowired
    private QuizAttemptRepository quizAttemptRepository;

    @Autowired
    private QuizAnswerRepository quizAnswerRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionOptionRepository questionOptionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserStatisticsRepository userStatisticsRepository;

    @Transactional
    public QuizResponse createQuiz(QuizRequest req) {
        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(req.getCategoryId()));

        int numQuestions = req.getNumQuestions() != null ? req.getNumQuestions() : 10;
        List<Question> randomQuestions = questionRepository.findRandomQuestionsByCategory(
                req.getCategoryId(), numQuestions);

        if (randomQuestions.size() < numQuestions) {
            throw new InsufficientQuestionsException(numQuestions, randomQuestions.size());
        }

        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Quiz quiz = Quiz.builder()
                .title(req.getTitle())
                .description(null)
                .category(category)
                .difficulty(req.getDifficulty() != null ? req.getDifficulty() : "MIXED")
                .timeLimitMinutes(req.getTimeLimitMinutes())
                .passingScorePct(req.getPassingScorePct() != null ? req.getPassingScorePct() : 60.0)
                .totalQuestions(numQuestions)
                .totalPoints(randomQuestions.stream().mapToInt(Question::getPoints).sum())
                .createdBy(currentUser)
                .build();
        quiz = quizRepository.save(quiz);

        List<QuizQuestion> quizQuestions = new ArrayList<>();
        for (int i = 0; i < randomQuestions.size(); i++) {
            Question q = randomQuestions.get(i);
            quizQuestions.add(QuizQuestion.builder()
                    .quiz(quiz)
                    .questionId(q.getId())
                    .sortOrder(i)
                    .build());
        }
        quizQuestionRepository.saveAll(quizQuestions);

        Map<Long, List<QuestionOption>> optionsMap = new HashMap<>();
        for (Question q : randomQuestions) {
            optionsMap.put(q.getId(), questionOptionRepository.findByQuestionIdOrderBySortOrder(q.getId()));
        }
        Map<Long, Question> questionMap = randomQuestions.stream()
                .collect(Collectors.toMap(Question::getId, q -> q));

        return QuizMapper.toResponse(quiz, quizQuestions, questionMap, optionsMap);
    }

    @Transactional(readOnly = true)
    public QuizResponse getQuizQuestions(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new QuizNotFoundException(id));

        List<QuizQuestion> quizQuestions = quizQuestionRepository.findByQuizIdOrderBySortOrder(id);

        Map<Long, Question> questionMap = new HashMap<>();
        Map<Long, List<QuestionOption>> optionsMap = new HashMap<>();
        for (QuizQuestion qq : quizQuestions) {
            if (!questionMap.containsKey(qq.getQuestionId())) {
                Question question = questionRepository.findById(qq.getQuestionId()).orElse(null);
                if (question != null) {
                    questionMap.put(qq.getQuestionId(), question);
                    optionsMap.put(qq.getQuestionId(),
                            questionOptionRepository.findByQuestionIdOrderBySortOrder(qq.getQuestionId()));
                }
            }
        }

        return QuizMapper.toResponse(quiz, quizQuestions, questionMap, optionsMap);
    }

    @Transactional
    public QuizScoreResponse submitQuiz(Long quizId, List<SubmitAnswerRequest> responses) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new QuizNotFoundException(quizId));

        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<QuizQuestion> quizQuestions = quizQuestionRepository.findByQuizIdOrderBySortOrder(quizId);

        Map<Long, Question> questionMap = new HashMap<>();
        Map<Long, List<QuestionOption>> optionsMap = new HashMap<>();
        for (QuizQuestion qq : quizQuestions) {
            if (!questionMap.containsKey(qq.getQuestionId())) {
                Question question = questionRepository.findById(qq.getQuestionId()).orElse(null);
                if (question != null) {
                    questionMap.put(qq.getQuestionId(), question);
                    optionsMap.put(qq.getQuestionId(),
                            questionOptionRepository.findByQuestionIdOrderBySortOrder(qq.getQuestionId()));
                }
            }
        }

        int totalPoints = 0;
        int earnedPoints = 0;
        int correctCount = 0;

        for (SubmitAnswerRequest req : responses) {
            Question question = questionMap.get(req.getQuestionId());
            if (question != null) {
                totalPoints += question.getPoints();
            }
        }

        List<QuizAnswer> answers = new ArrayList<>();
        for (SubmitAnswerRequest req : responses) {
            Question question = questionMap.get(req.getQuestionId());
            if (question == null) continue;

            List<QuestionOption> options = optionsMap.getOrDefault(req.getQuestionId(), Collections.emptyList());
            boolean isCorrect = false;
            if (req.getSelectedOptionId() != null) {
                isCorrect = options.stream()
                        .anyMatch(opt -> opt.getId().equals(req.getSelectedOptionId()) && opt.isCorrect());
            }

            int pointsEarned = isCorrect ? question.getPoints() : 0;
            earnedPoints += pointsEarned;
            if (isCorrect) correctCount++;

            answers.add(QuizAnswer.builder()
                    .questionId(req.getQuestionId())
                    .selectedOptionId(req.getSelectedOptionId())
                    .answerText(req.getAnswerText())
                    .isCorrect(isCorrect)
                    .pointsEarned(pointsEarned)
                    .build());
        }

        double scorePct = totalPoints > 0 ? ((double) earnedPoints / totalPoints) * 100 : 0.0;
        boolean passed = scorePct >= (quiz.getPassingScorePct() != null ? quiz.getPassingScorePct() : 60.0);
        Instant now = Instant.now();

        QuizAttempt attempt = QuizAttempt.builder()
                .quizId(quizId)
                .userId(currentUser.getId())
                .startedAt(now)
                .submittedAt(now)
                .score(earnedPoints)
                .maxScore(totalPoints)
                .scorePct(scorePct)
                .passed(passed)
                .status("COMPLETED")
                .build();
        attempt = quizAttemptRepository.save(attempt);

        Long attemptId = attempt.getId();
        for (QuizAnswer answer : answers) {
            answer.setAttemptId(attemptId);
            answer.setCreatedAt(now);
        }
        quizAnswerRepository.saveAll(answers);

        updateUserStatistics(currentUser, attempt, correctCount, responses.size());

        return QuizMapper.toScoreResponse(attempt, quiz, answers);
    }

    private void updateUserStatistics(User user, QuizAttempt attempt, int correctCount, int totalAnswered) {
        UserStatistics stats = userStatisticsRepository.findByUserId(user.getId())
                .orElseGet(() -> UserStatistics.builder()
                        .userId(user.getId())
                        .build());

        stats.setTotalQuizzesTaken(stats.getTotalQuizzesTaken() + 1);
        if (Boolean.TRUE.equals(attempt.getPassed())) {
            stats.setTotalQuizzesPassed(stats.getTotalQuizzesPassed() + 1);
        }
        stats.setTotalQuestionsAnswered(stats.getTotalQuestionsAnswered() + totalAnswered);
        stats.setTotalCorrectAnswers(stats.getTotalCorrectAnswers() + correctCount);
        stats.setTotalScore(stats.getTotalScore() + attempt.getScore());
        stats.setTotalXp(stats.getTotalXp() + attempt.getScore());
        stats.setLastActiveAt(Instant.now());
        stats.setLastQuizAt(Instant.now());

        if (stats.getTotalQuizzesTaken() > 0) {
            double avgPct = (double) stats.getTotalCorrectAnswers() / stats.getTotalQuestionsAnswered() * 100;
            stats.setAverageScorePct(avgPct);
        }

        userStatisticsRepository.save(stats);
    }
}
