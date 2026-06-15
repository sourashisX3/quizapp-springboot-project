package com.sourashis.quizapp.modules.quiz.service;

import com.sourashis.quizapp.modules.analytics.entity.UserStatistics;
import com.sourashis.quizapp.modules.analytics.repository.UserStatisticsRepository;
import com.sourashis.quizapp.modules.auth.entity.User;
import com.sourashis.quizapp.modules.auth.repository.UserRepository;
import com.sourashis.quizapp.modules.quiz.dto.QuizSummaryResponse;
import com.sourashis.quizapp.modules.quiz.entity.Category;
import com.sourashis.quizapp.modules.quiz.entity.Quiz;
import com.sourashis.quizapp.modules.quiz.repository.CategoryRepository;
import com.sourashis.quizapp.modules.quiz.repository.QuizAttemptRepository;
import com.sourashis.quizapp.modules.quiz.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class QuizDiscoveryService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private QuizAttemptRepository quizAttemptRepository;

    @Autowired
    private UserStatisticsRepository userStatisticsRepository;

    @Autowired
    private UserRepository userRepository;

    public List<QuizSummaryResponse> getRecentQuizzes(int limit) {
        Page<Quiz> quizzes = quizRepository.findByIsPublishedTrue(PageRequest.of(0, limit));
        return quizzes.stream().map(this::toSummaryResponse).toList();
    }

    public Page<QuizSummaryResponse> getQuizzesByCategory(Long categoryId, Pageable pageable) {
        return quizRepository.findByCategoryId(categoryId, pageable).map(this::toSummaryResponse);
    }

    public List<Map<String, Object>> getTrendingCategories() {
        List<Category> categories = categoryRepository.findByIsActiveTrue();
        List<Map<String, Object>> trending = new ArrayList<>();
        for (Category cat : categories) {
            long attemptCount = 0;
            List<Quiz> quizzes = quizRepository.findByCategoryId(cat.getId(), Pageable.unpaged()).getContent();
            for (Quiz quiz : quizzes) {
                attemptCount += quizAttemptRepository.findByQuizId(quiz.getId()).size();
            }
            if (attemptCount > 0) {
                Map<String, Object> entry = new HashMap<>();
                entry.put("categoryId", cat.getId());
                entry.put("name", cat.getName());
                entry.put("attemptCount", attemptCount);
                trending.add(entry);
            }
        }
        trending.sort((a, b) -> Long.compare((Long) b.get("attemptCount"), (Long) a.get("attemptCount")));
        return trending;
    }

    public Page<QuizSummaryResponse> searchQuizzes(String query, Pageable pageable) {
        return quizRepository.findByIsPublishedTrue(pageable)
                .map(this::toSummaryResponse);
    }

    public List<QuizSummaryResponse> getRecommendedQuizzes(Long userId) {
        UserStatistics stats = userStatisticsRepository.findByUserId(userId).orElse(null);
        if (stats == null || stats.getTotalQuizzesTaken() == 0) {
            return getRecentQuizzes(10);
        }

        Map<Long, Long> categoryAttempts = new HashMap<>();
        List<Long> attemptedQuizIds = quizAttemptRepository.findByUserId(userId)
                .stream().map(a -> a.getQuizId()).distinct().toList();

        for (Long quizId : attemptedQuizIds) {
            quizRepository.findById(quizId).ifPresent(quiz -> {
                if (quiz.getCategory() != null) {
                    Long catId = quiz.getCategory().getId();
                    categoryAttempts.merge(catId, 1L, Long::sum);
                }
            });
        }

        Long topCategoryId = categoryAttempts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        if (topCategoryId == null) {
            return getRecentQuizzes(10);
        }

        return quizRepository.findByCategoryId(topCategoryId, PageRequest.of(0, 10))
                .stream().map(this::toSummaryResponse).toList();
    }

    public Page<QuizSummaryResponse> getAllPublishedQuizzes(Pageable pageable) {
        return quizRepository.findByIsPublishedTrue(pageable).map(this::toSummaryResponse);
    }

    private QuizSummaryResponse toSummaryResponse(Quiz quiz) {
        String createdByUsername = null;
        if (quiz.getCreatedBy() != null) {
            createdByUsername = userRepository.findById(quiz.getCreatedBy().getId())
                    .map(User::getUsername).orElse(null);
        }

        return QuizSummaryResponse.builder()
                .id(quiz.getId())
                .uuid(quiz.getUuid())
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .categoryId(quiz.getCategory() != null ? quiz.getCategory().getId() : null)
                .categoryName(quiz.getCategory() != null ? quiz.getCategory().getName() : null)
                .difficulty(quiz.getDifficulty())
                .timeLimitMinutes(quiz.getTimeLimitMinutes())
                .totalQuestions(quiz.getTotalQuestions())
                .totalPoints(quiz.getTotalPoints())
                .maxAttempts(quiz.getMaxAttempts())
                .isPublished(quiz.getIsPublished())
                .createdAt(quiz.getCreatedAt())
                .createdByUsername(createdByUsername)
                .build();
    }
}
