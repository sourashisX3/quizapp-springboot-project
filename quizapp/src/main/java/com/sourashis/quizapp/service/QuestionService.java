package com.sourashis.quizapp.service;

import com.sourashis.quizapp.dao.QuestionDao;
import com.sourashis.quizapp.entities.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionDao questionDao;

    // -- Get all questions --
    public ResponseEntity<List<Question>> getAllQuestions() {
        try {
            return new ResponseEntity<>(questionDao.findAll(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
    }

    // -- Get questions by category --
    public List<Question> getQuestionsByCategory(String category) {
        return questionDao.findByCategory(category);
    }


    // -- Add question --
    public String addQuestion(Question question) {
        questionDao.save(question);
        return "Question added successfully!";
    }


    // -- Delete question --
    public String deleteQuestionById(int id) {
        questionDao.deleteById(id);
        return "Question deleted successfully!";
    }
}
