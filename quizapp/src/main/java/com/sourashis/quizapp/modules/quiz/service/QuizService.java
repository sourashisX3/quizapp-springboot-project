package com.sourashis.quizapp.modules.quiz.service;

import com.sourashis.quizapp.modules.question.entity.Question;
import com.sourashis.quizapp.modules.question.repository.QuestionRepository;
import com.sourashis.quizapp.modules.quiz.dto.QuizRequest;
import com.sourashis.quizapp.modules.quiz.dto.QuizResponse;
import com.sourashis.quizapp.modules.quiz.dto.QuizScoreResponse;
import com.sourashis.quizapp.modules.quiz.dto.SubmitAnswerRequest;
import com.sourashis.quizapp.modules.quiz.entity.Quiz;
import com.sourashis.quizapp.modules.quiz.exception.QuizNotFoundException;
import com.sourashis.quizapp.modules.quiz.mapper.QuizMapper;
import com.sourashis.quizapp.modules.quiz.repository.QuizRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuizService {

    private static final Logger log = LoggerFactory.getLogger(QuizService.class);

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionRepository questionRepository;

    // -- Create quiz --
    @Transactional
    public QuizResponse createQuiz(QuizRequest request) {
        log.info("Creating quiz '{}' with {} questions in category '{}'",
                request.getTitle(), request.getNumQuestions(), request.getCategory());

        List<Question> questions = questionRepository.findRandomQuestionsByCategory(
                request.getCategory(), request.getNumQuestions());

        Quiz quiz = new Quiz();
        quiz.setTitle(request.getTitle());
        quiz.setQuestions(questions);

        Quiz saved = quizRepository.save(quiz);
        return QuizMapper.toResponse(saved);
    }

    // -- Get quiz questions --
    public QuizResponse getQuizQuestions(Integer id) {
        log.info("Fetching questions for quiz id: {}", id);
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new QuizNotFoundException(id));
        return QuizMapper.toResponse(quiz);
    }

    // -- Calculate score --
    public QuizScoreResponse calculateScore(Integer id, List<SubmitAnswerRequest> responses) {
        log.info("Calculating score for quiz id: {}", id);
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new QuizNotFoundException(id));

        List<Question> questions = quiz.getQuestions();
        int correct = 0;

        for (int i = 0; i < responses.size(); i++) {
            if (responses.get(i).getResponse().equals(questions.get(i).getRightAnswer())) {
                correct++;
            }
        }

        int total = questions.size();
        int wrong = total - correct;
        double percentage = total > 0 ? ((double) correct / total) * 100 : 0;

        return QuizScoreResponse.builder()
                .quizId(quiz.getId())
                .quizTitle(quiz.getTitle())
                .totalQuestions(total)
                .correctAnswers(correct)
                .wrongAnswers(wrong)
                .scorePercentage(Math.round(percentage * 100.0) / 100.0)
                .build();
    }
}

