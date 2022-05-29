package com.kamilla.deppplom.question.repository;

import com.kamilla.deppplom.question.model.Question;
import com.kamilla.deppplom.question.model.Selection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface QuestionJpaRepository extends JpaRepository<QuestionEntity, Integer> {

    List<QuestionEntity> findAllByDisciplineId(int id);

}
