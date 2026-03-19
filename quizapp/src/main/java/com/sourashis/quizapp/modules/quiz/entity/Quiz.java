package com.sourashis.quizapp.modules.quiz.entity;

import com.sourashis.quizapp.modules.question.entity.Question;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    @ManyToMany
    private List<Question> questions;
}

