package com.sourashis.quizapp.service;

import com.sourashis.quizapp.dao.QuestionDao;
import com.sourashis.quizapp.dao.QuizDao;
import com.sourashis.quizapp.models.Question;
import com.sourashis.quizapp.models.QuestionWrapper;
import com.sourashis.quizapp.models.Quiz;
import com.sourashis.quizapp.models.SubmitResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuizService {

    @Autowired
    QuizDao quizDao;

    @Autowired
    QuestionDao questionDao;

    public ResponseEntity<String> createQuiz(String category, Integer numQ, String title) {

        List<Question> questions = questionDao.findRandomQuestionsByCategory(category, numQ);

        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setQuestions(questions);
        quizDao.save(quiz);

        return new ResponseEntity<>("Success", HttpStatus.CREATED);
    }

    public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(Integer id) {
        Optional<Quiz> quiz = quizDao.findById(id);

        List<Question> questionsFromDB = quiz.get().getQuestions();
        List<QuestionWrapper> questionsForUser = new ArrayList<>();

        for (Question question : questionsFromDB) {
            QuestionWrapper questionWrapper = new QuestionWrapper(
                    question.getOption4(),
                    question.getOption3(), question.getOption2(),
                    question.getOption1(),
                    question.getQuestionTitle(),
                    question.getId());
            questionsForUser.add(questionWrapper);
        }

        return new ResponseEntity<>(questionsForUser, HttpStatus.OK);
    }

    public ResponseEntity<Integer> calculateScore(Integer id, List<SubmitResponse> responses) {

        Quiz quiz = quizDao.findById(id).get();
        List<Question> questions = quiz.getQuestions();

        int right = 0;
        int i = 0;
        for (SubmitResponse submitResponse : responses) {
            if (submitResponse.getResponse().equals(questions.get(i).getRightAnswer()))
                right++;
            i++;
        }
        return new ResponseEntity<>(right, HttpStatus.OK);
    }
}
