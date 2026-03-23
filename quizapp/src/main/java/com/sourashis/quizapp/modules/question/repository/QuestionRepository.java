package com.sourashis.quizapp.modules.question.repository;

import com.sourashis.quizapp.modules.question.entity.Question;
import com.sourashis.quizapp.modules.quiz.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {

    List<Question> findByCategory(Category category);

    @Query(value = "SELECT * FROM question q WHERE q.category_id = ?1 ORDER BY RAND() LIMIT ?2", nativeQuery = true)
    List<Question> findRandomQuestionsByCategory(Integer categoryId, Integer numQ);
}

