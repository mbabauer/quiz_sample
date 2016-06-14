package com.codechimp.quiz.controllers;

import com.codechimp.quiz.domain.AnswerRepository;
import com.codechimp.quiz.domain.QuestionRepository;
import com.codechimp.quiz.entities.Answer;
import com.codechimp.quiz.entities.Question;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Controller for the Quiz Admin
 *
 * @author Mike Bauer
 * @version 0.0.1
 */
@Controller
@RequestMapping("/admin")
public class QuizAdminController {
    @Autowired
    private QuestionRepository questionRepo;

    @Autowired
    private AnswerRepository answerRepo;

    @Autowired
    private PlatformTransactionManager txManager;

    @Autowired
    private MessageSource messageSource;

    public static Log _log = LogFactory.getLog(QuizAdminController.class);

    /**
     * Request handler for root path of /admin site
     * @return The view name
     */
    @RequestMapping
    public String index(@ModelAttribute("errors") ArrayList<String> errors, Model model) {
        model.addAttribute("questions", questionRepo.findAll());
        model.addAttribute("errors", errors);
        return "admin/index";
    }

    /**
     * Request handler for displaying the Add Question page
     * @param question The new question
     * @param model The Model
     * @return The name of the view
     */
    @RequestMapping(value = "/addQuestion", method = RequestMethod.GET)
    public String addQuestion(@ModelAttribute Question question, Model model) {
        model.addAttribute("question", question);
        return "admin/add_question";
    }

    /**
     * Request handler for saving the new Question
     * @param question The Question with form values
     * @param results The binding results from validation
     * @return A ModelAndView
     */
    @RequestMapping(value = "/addQuestion", method = RequestMethod.POST)
    public ModelAndView addQuestion(@ModelAttribute @Valid Question question, BindingResult results) {
        ModelAndView mav = null;
        try {
            if (!results.hasErrors()) {
                questionRepo.save(question);
                mav = new ModelAndView("redirect:/admin");
            }
        } finally {
            if (mav == null) {
                mav = new ModelAndView("admin/add_question");
                mav.addObject("question", question);
            }
        }

        return mav;
    }

    /**
     * Deletes a Question and it'a Answers
     * @param questionId The question id to remove
     * @return A ModelAndView
     */
    @RequestMapping(value = "/removeQuestion")
    public ModelAndView removeQuestion(@RequestParam(name = "questionId") Long questionId) {
        ModelAndView mav = new ModelAndView("redirect:/admin");;
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setName("SomeTxName");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        TransactionStatus status = txManager.getTransaction(def);
        try {
            Question question = questionRepo.findOne(questionId);
            if (question != null) {
                for (Answer ans : question.getAnswers()) {
                    answerRepo.delete(ans);
                }
                questionRepo.delete(questionId);
            }
            txManager.commit(status);
        } catch(Exception e) {
            txManager.rollback(status);
            throw e;
        }

        return mav;
    }

    /**
     * Updates the given question
     * @param question The new question values
     * @param results The BindingResults
     * @param locale The user's locale
     * @param redirectAttributes The RedirectAttributes
     * @return A ModelAndView
     */
    @RequestMapping(value = "/updateQuestion")
    public ModelAndView updateQuestion(
            @ModelAttribute @Valid Question question,
            BindingResult results,
            Locale locale,
            RedirectAttributes redirectAttributes ) {
        ModelAndView mav = new ModelAndView("redirect:/admin");
        List<String> errors = new ArrayList<>();
        try {
            if (!results.hasErrors()) {
                Question oldQuestion = questionRepo.findOne(question.getId());
                if (oldQuestion != null) {
                    question.setAnswers(oldQuestion.getAnswers());
                }
                questionRepo.save(question);
            } else {
                // Convert the bound errors to "general" errors, since we do this all on the same page at the top after page load
                for (ObjectError err : results.getAllErrors()) {
                    errors.add(messageSource.getMessage(err, locale));
                }
            }
        } catch(Exception e) {
            errors.add("An unknown error occurred: " + e.getLocalizedMessage());
        }

        if (errors.size() > 0) {
            redirectAttributes.addFlashAttribute("errors", errors);
        }
        return mav;
    }

    /**
     * Request handler for saving the new Answer
     * @param answer The answer w/ form values
     * @param results The binding results from validation
     * @param questionId The question ID
     * @param locale The user's locale
     * @param redirectAttributes The RedirectAttributes
     * @return The view name
     */
    @RequestMapping(value = "/addAnswer", method = RequestMethod.POST)
    public ModelAndView addAnswer(
            @ModelAttribute @Valid Answer answer,
            BindingResult results,
            @RequestParam(name = "questionID") Long questionId,
            Locale locale,
            RedirectAttributes redirectAttributes ) {
        ModelAndView mav = new ModelAndView("redirect:/admin");
        List<String> errors = new ArrayList<>();
        try {
            if (!results.hasErrors()) {
                Question question = questionRepo.findOne(questionId);
                if (question != null) {
                    List<Answer> newAnswers = question.getAnswers() == null ? new ArrayList<>() : question.getAnswers();

                    answer.setQuestion(question);
                    if (answer.isCorrectAnswer()) { // Make sure no other answers are marked as the correct answer
                        for (Answer ans : newAnswers) {
                            if (ans.isCorrectAnswer()) {
                                ans.setCorrectAnswer(false);
                                answerRepo.save(ans);
                            }
                        }
                    }
                    newAnswers.add(answer);
                    question.setAnswers(newAnswers);
                    answerRepo.save(answer);
                    questionRepo.save(question);
                }
            } else {
                // Convert the bound errors to "general" errors, since we do this all on the same page at the top after page load
                for (ObjectError err : results.getAllErrors()) {
                    errors.add(messageSource.getMessage(err, locale));
                }
            }
        } catch(Exception e) {
            errors.add("An unknown error occurred: " + e.getLocalizedMessage());
        }

        if (errors.size() > 0) {
            redirectAttributes.addFlashAttribute("errors", errors);
        }
        return mav;
    }

    /**
     * Removes an answer
     * @param questionId The question id
     * @param answerId The answer id
     * @param redirectAttributes The RedirectAttributes
     * @return A ModelAndView
     */
    @RequestMapping(value = "/removeAnswer")
    public ModelAndView removeAnswer(
            @RequestParam(name = "questionId") Long questionId,
            @RequestParam(name = "answerId") Long answerId,
            RedirectAttributes redirectAttributes ) {
        ModelAndView mav = new ModelAndView("redirect:/admin");
        List<String> errors = new ArrayList<>();
        try {
            Question question = questionRepo.findOne(questionId);
            if (question != null) {
                Answer answer = answerRepo.findOne(answerId);
                if (answer != null) {
                    question.getAnswers().remove(answer);
                    answerRepo.delete(answer);
                    questionRepo.save(question);
                }
            }
        } catch(Exception e) {
            errors.add("An unknown error occurred: " + e.getLocalizedMessage());
        }

        if (errors.size() > 0) {
            redirectAttributes.addFlashAttribute("errors", errors);
        }

        return mav;
    }

    /**
     * Updates an answer
     * @param answer The new answer values
     * @param results The BindingResults
     * @param questionId The question id
     * @param locale The user's locale
     * @param redirectAttributes The RedirectAttributes
     * @return A ModelAndView
     */
    @RequestMapping(value = "/updateAnswer")
    public ModelAndView updateAnswer(
            @ModelAttribute @Valid Answer answer,
            BindingResult results,
            @RequestParam(name = "questionId") Long questionId,
            Locale locale,
            RedirectAttributes redirectAttributes ) {
        ModelAndView mav = new ModelAndView("redirect:/admin");
        List<String> errors = new ArrayList<>();
        try {
            if (!results.hasErrors()) {
                Question question = questionRepo.findOne(questionId);
                if (question != null) {
                    List<Answer> existingAnswers = question.getAnswers() == null ? new ArrayList<>() : question.getAnswers();

                    answer.setQuestion(question);
                    if (answer.isCorrectAnswer()) { // Make sure no other answers are marked as the correct answer
                        for (Answer ans : existingAnswers) {
                            if (ans.isCorrectAnswer() && ans.getId() != answer.getId()) {
                                ans.setCorrectAnswer(false);
                                answerRepo.save(ans);
                            }
                        }
                    }
                    answerRepo.save(answer);
                }
            } else {
                // Convert the bound errors to "general" errors, since we do this all on the same page at the top after page load
                for (ObjectError err : results.getAllErrors()) {
                    errors.add(messageSource.getMessage(err, locale));
                }
            }
        } catch(Exception e) {
            errors.add("An unknown error occurred: " + e.getLocalizedMessage());
        }

        if (errors.size() > 0) {
            redirectAttributes.addFlashAttribute("errors", errors);
        }

        return mav;
    }

    /**
     * Request handler for the login path for the /admin site
     * @return The view name
     */
    @RequestMapping("/login")
    public String login() {
        return "admin/login";
    }

    /**
     * Creates a new Question
     * @return The newly created Question
     */
    @ModelAttribute
    private Question createQuestion() {
        return new Question();
    }

    /**
     * Creates a new Answer
     * @return Tne newly created Answer
     */
    @ModelAttribute
    private Answer createAnswer() {
        return new Answer();
    }

    /**
     * Sets up custom binding rules for the data binder
     * @param binder The data binder
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        StringTrimmerEditor stringTrimmer = new StringTrimmerEditor(true);
        binder.registerCustomEditor(String.class, stringTrimmer);
    }
}
