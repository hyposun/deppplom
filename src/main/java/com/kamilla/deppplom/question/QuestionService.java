package com.kamilla.deppplom.question;

import com.kamilla.deppplom.question.model.CheckResult;
import com.kamilla.deppplom.question.model.Question;
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

    public Question save(Question question) {
        return repository.save(question);
    }

    public Optional<Question> findQuestionById(int id) {
        return repository.findQuestionById(id);
    }

    public Question getQuestionById(int id) {
        return findQuestionById(id)
                       .orElseThrow(() -> new IllegalArgumentException("Вопрос не найден"));
    }

    public void deleteById(int id) {
        repository.deleteById(id);
    }

    public CheckResult check(int questionId, Selection selection) {
        return repository
                .findQuestionById(questionId)
                .map(question -> question.check(selection))
                .orElseThrow(() -> new IllegalArgumentException("Вопрос с идентификтором " + questionId + " не найден"));
    }

    public List<Question> findAll() {
        return repository.findAll();
    }

    public List<Question> findQuestionsByDisciplineId(int disciplineId){
        return repository.findAllByDisciplineId(disciplineId);
    }


}