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
    public ResponseEntity<List<Question>> getQuestionsByCategory(String category) {
        try {
            return new ResponseEntity<>(questionDao.findByCategory(category), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
    }


    // -- Add question --
    public ResponseEntity<String> addQuestion(Question question) {
        try {
            questionDao.save(question);
            return new ResponseEntity<>("Question added successfully!", HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>("Failed to add question!", HttpStatus.BAD_REQUEST);
    }


    // -- Delete question --
    public ResponseEntity<String> deleteQuestionById(int id) {
        try {
            questionDao.deleteById(id);
            return new ResponseEntity<>("Question deleted successfully!", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>("Failed to delete question!", HttpStatus.BAD_REQUEST);
    }
}
