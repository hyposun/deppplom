package com.kamilla.deppplom.question.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionJpaRepository extends JpaRepository<QuestionEntity, Integer> {

    List<QuestionEntity> findAllByDisciplineId(int id);

}
