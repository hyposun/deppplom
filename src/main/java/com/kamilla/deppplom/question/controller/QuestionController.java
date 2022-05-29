package com.kamilla.deppplom.question.controller;

import com.kamilla.deppplom.question.QuestionService;
import com.kamilla.deppplom.question.impl.closedquestion.ClosedQuestion;
import com.kamilla.deppplom.question.impl.orderedclosedquestion.OrderedClosedQuestion;
import com.kamilla.deppplom.question.model.Question;
import com.kamilla.deppplom.question.model.Selection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v0/questions")
public class QuestionController {

    @Autowired
    private QuestionService service;

    @PostMapping("/create/closed_question")
    public ClosedQuestion create(@RequestBody @Valid ClosedQuestion question) {
        return (ClosedQuestion) service.save(question);
    }

    @PostMapping("/create/order_close_question")
    public OrderedClosedQuestion create(@RequestBody @Valid OrderedClosedQuestion question){
        return (OrderedClosedQuestion) service.save(question);
    }

    @GetMapping
    public List<Question> findAll(@RequestParam(required = false) Integer disciplineId) {
        if (disciplineId!=null){
            return service.findQuestionsByDisciplineId(disciplineId);
        } else {
            return service.findAll();
        }
    }

}
