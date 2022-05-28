package com.kamilla.deppplom.examination.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionExaminationRepository extends JpaRepository<QuestionExaminationEntity, Integer> {

}
