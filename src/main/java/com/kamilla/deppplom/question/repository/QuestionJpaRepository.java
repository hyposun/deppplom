package com.kamilla.deppplom.question.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface QuestionJpaRepository extends JpaRepository<QuestionEntity, Integer> {

}
