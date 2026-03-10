package com.sourashis.quizapp.dao;

import com.sourashis.quizapp.models.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizDao extends JpaRepository<Quiz, Integer> {
}
