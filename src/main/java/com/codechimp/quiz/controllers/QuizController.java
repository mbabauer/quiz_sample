package com.codechimp.quiz.controllers;

import com.codechimp.quiz.domain.AnswerRepository;
import com.codechimp.quiz.domain.QuestionRepository;
import com.codechimp.quiz.domain.QuestionResponseRepository;
import com.codechimp.quiz.domain.QuizResponseRepository;
import com.codechimp.quiz.entities.Question;
import com.codechimp.quiz.entities.QuestionResponse;
import com.codechimp.quiz.entities.QuizResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for Quiz
 */
@Controller
@SessionAttributes({"quizResponse"})
public class QuizController {

    @Autowired
    private QuestionRepository questionRepo;

    @Autowired
    private QuestionResponseRepository questionResponseRepo;

    @Autowired
    private QuizResponseRepository quizResponseRepo;

    @Autowired
    private AnswerRepository answerRepo;

    @Autowired
    private PlatformTransactionManager txManager;

    private static final Log _log = LogFactory.getLog(QuizController.class);

    @RequestMapping
    public String index(@ModelAttribute("quizResponse") QuizResponse quizResponse, Model model) {
        model.addAttribute("quizResponse", quizResponse);
        return "quiz/index";
    }

    @RequestMapping(value = "/quiz")
    public String quiz(
            @ModelAttribute("quizResponse") @Valid QuizResponse quizResponse,
            BindingResult results,
            @RequestParam(name = "currentQuestionResponseIdx", required = false) Integer currentQuestionResponseIdx,
            @RequestParam(name = "answerId", required = false) Long answerId,
            Model model) {
        String view = null;

        try {
            if (quizResponse.getId() < 1) {
                QuizResponse duplicateQuizResponse = quizResponseRepo.findByEmail(quizResponse.getEmail());
                if (duplicateQuizResponse != null) {
                    // Use previously saved response
                    quizResponse = duplicateQuizResponse;
                } else {
                    // Save the quiz and it's responses for the first time
                    for (QuestionResponse questionResponse : quizResponse.getResponses()) {
                        questionResponseRepo.save(questionResponse);
                    }
                    quizResponseRepo.save(quizResponse);
                }
            }

            if (quizResponse.isCompleted()) {
                view = "redirect:/results";
            } else {
                if (currentQuestionResponseIdx != null) {
                    QuestionResponse currentQuestionResponse = currentQuestionResponseIdx < quizResponse.getResponses().size() ? quizResponse.getResponses().get(currentQuestionResponseIdx) : null;
                    ;
                    if (currentQuestionResponse != null) {
                        // Save the response if the user provided an answer
                        if (currentQuestionResponse != null && answerId != null) {
                            currentQuestionResponse.setAnswer(answerRepo.findOne(answerId));
                            questionResponseRepo.save(currentQuestionResponse);
                        }

                        currentQuestionResponseIdx++;
                        if (currentQuestionResponseIdx < quizResponse.getResponses().size()) {
                            currentQuestionResponse = quizResponse.getResponses().get(currentQuestionResponseIdx);
                            model.addAttribute("currentQuestionResponse", currentQuestionResponse);
                            model.addAttribute("currentQuestionResponseIdx", currentQuestionResponseIdx);
                            view = "quiz/question";
                        } else {
                            view = "quiz/submit";
                        }
                    }
                } else {
                    model.addAttribute("currentQuestionResponse", quizResponse.getResponses().size() > 0 ? quizResponse.getResponses().get(0) : null);
                    model.addAttribute("currentQuestionResponseIdx", 0);
                    view = "quiz/question";
                }
            }
        } catch(Exception e) {
            _log.error("Error saving QuizResponse", e);
            throw e;
        }
        model.addAttribute("quizResponse", quizResponse);

        return view;
    }

    @RequestMapping("/results")
    public String results(@ModelAttribute("quizResponse") @Valid QuizResponse quizResponse,
                           BindingResult results,
                           Model model ) {
        quizResponse.setCompleted(true);
        quizResponseRepo.save(quizResponse);
        int correctCnt = 0;
        int incorrectCnt = 0;
        for (QuestionResponse qResp : quizResponse.getResponses()) {
            if (qResp.getAnswer() != null && qResp.getAnswer().getId() == qResp.getCorrectAnswer().getId()) {
                correctCnt++;
            } else {
                incorrectCnt++;
            }
        }
        model.addAttribute("correctCnt", correctCnt);
        model.addAttribute("incorrectCnt", incorrectCnt);
        model.addAttribute("totalCnt", quizResponse.getResponses().size());
        return "/quiz/results";
    }

    @ModelAttribute
    private QuizResponse createQuizResponse() {
        QuizResponse response = new QuizResponse();

        List<QuestionResponse> questionResponses = new ArrayList<>();
        for (Question question : questionRepo.findAll()) {
            questionResponses.add(new QuestionResponse(question));
        }
        response.setResponses(questionResponses);

        return response;
    }
}
