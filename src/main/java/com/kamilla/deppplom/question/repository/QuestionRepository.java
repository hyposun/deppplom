package com.kamilla.deppplom.question.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kamilla.deppplom.question.impl.closedquestion.ClosedQuestion;
import com.kamilla.deppplom.question.impl.orderedclosedquestion.OrderedClosedQuestion;
import com.kamilla.deppplom.question.model.Question;
import com.kamilla.deppplom.question.model.Selection;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class QuestionRepository {

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private QuestionJpaRepository repository;

    public Optional<Question<Selection>> findQuestionById(int id) {
        return repository.findById(id)
                .map(this::fromEntity);
    }

    @SneakyThrows
    public <T extends Selection> Question<T> save(Question<T> question) {
        QuestionEntity entity = new QuestionEntity();
        entity.setTitle(question.getTitle());
        entity.setType(question.getType());
        entity.setDisciplineId(question.getDiscipline_id());
        entity.setBody(mapper.writeValueAsString(question));
        entity = repository.save(entity);
        return fromEntity(entity);
    }

    public List<Question<Selection>> findAll() {
        return repository.findAll()
                .stream().map(this::fromEntity)
                .collect(Collectors.toList());
    }

    public List<Question<Selection>> findAllByDisciplineId(int disciplineId){
        return repository.findAllByDisciplineId(disciplineId);
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    private <T extends Selection> Question<T> fromEntity(QuestionEntity entity) {
        Class type;
        switch (entity.getType()) {
            case ClosedQuestion.TYPE:
                type = ClosedQuestion.class;
                break;
            case OrderedClosedQuestion.TYPE:
                type = OrderedClosedQuestion.class;
                break;
            default:
                throw new IllegalArgumentException("Неизвестный тип вопроса: " + entity.getType());
        }
        Question question = (Question) mapper.readValue(entity.getBody(), type);
        question.setId(entity.getId());
        return question;
    }

}