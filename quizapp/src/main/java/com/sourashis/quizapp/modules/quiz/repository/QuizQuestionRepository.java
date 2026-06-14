package com.sourashis.quizapp.modules.quiz.repository;

import com.sourashis.quizapp.modules.quiz.entity.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long> {

    List<QuizQuestion> findByQuizIdOrderBySortOrder(Long quizId);

    void deleteByQuizId(Long quizId);
}
