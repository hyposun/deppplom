package com.kamilla.deppplom.examination.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExaminationRepository extends JpaRepository<ExaminationEntity,Integer> {

    Optional<ExaminationEntity> findByStudentIdAndGroupExaminationId(
        int studentId,
        int groupExaminationId
    );

}


