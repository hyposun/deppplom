package com.kamilla.deppplom.question;

import com.kamilla.deppplom.question.model.Question;
import com.kamilla.deppplom.question.model.Result;
import com.kamilla.deppplom.question.model.Selection;
import com.kamilla.deppplom.question.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository repository;

    public <T extends Selection> Question<T> save(Question<T> question) {
        return repository.save(question);
    }

    public Optional<Question<Selection>> findQuestionById(int id) {
        return repository.findQuestionById(id);
    }

    public Result check(int questionId, Selection selection) {
        return repository
                .findQuestionById(questionId)
                .map(question -> question.check(selection))
                .orElseThrow(() -> new IllegalArgumentException("Вопрос с идентификтором " + questionId + " не найден"));
    }

    public List<Question<Selection>> findAll() {
        return repository.findAll();
    }
}