package com.kamilla.deppplom.question.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kamilla.deppplom.question.model.Question;
import com.kamilla.deppplom.question.model.QuestionType;
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

    public Optional<Question> findQuestionById(int id) {
        return repository.findById(id)
                .map(this::fromEntity);
    }

    @SneakyThrows
    public Question save(Question question) {
        QuestionEntity entity = new QuestionEntity();
        entity.setId(question.getId());
        entity.setTitle(question.getTitle());
        entity.setType(question.getType().getType());
        entity.setDisciplineId(question.getDisciplineId());
        entity.setBody(mapper.writeValueAsString(question));
        entity.setDisabled(question.isDisabled());
        entity = repository.save(entity);
        return fromEntity(entity);
    }

    public List<Question> findAll() {
        return repository.findAll()
                .stream().map(this::fromEntity)
                .collect(Collectors.toList());
    }

    public List<Question> findAllByDisciplineId(int disciplineId){
        return repository.findAllByDisciplineId(disciplineId)
                         .stream().map(this::fromEntity)
                         .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    private Question fromEntity(QuestionEntity entity) {

        QuestionType type = QuestionType.getByType(entity.getType());
        Class<? extends Question> clazz = type.getClazz();

        Question question = mapper.readValue(entity.getBody(), clazz);
        question.setId(entity.getId());
        return question;
    }

    public void deleteById(int id) {
        repository.deleteById(id);
    }

}