package com.kamilla.deppplom.question;

import com.kamilla.deppplom.discipline.Discipline;
import com.kamilla.deppplom.discipline.repository.DisciplineRepository;
import com.kamilla.deppplom.question.model.CheckResult;
import com.kamilla.deppplom.question.model.Question;
import com.kamilla.deppplom.question.model.Selection;
import com.kamilla.deppplom.question.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository repository;

    @Autowired
    private DisciplineRepository disciplineRepository;

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

    public List<Question> findAllByDisciplineHierarchy(int disciplineId) {
        Set<Integer> disciplines = getSubdisciplines(disciplineId);
        disciplines.add(disciplineId);
        return disciplines.stream()
                .flatMap(it -> repository.findAllByDisciplineId(it).stream())
                .collect(toList());
    }

    private Set<Integer> getSubdisciplines(int disciplineId) {
        Set<Integer> childs = disciplineRepository
                .findAllByParentId(disciplineId).stream().map(Discipline::getId).collect(Collectors.toSet());
        return childs.stream()
                .flatMap(it -> getSubdisciplines(it).stream())
                .collect(toCollection(() -> childs));
    }


}