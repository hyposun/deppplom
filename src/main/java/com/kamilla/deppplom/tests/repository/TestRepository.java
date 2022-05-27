package com.kamilla.deppplom.tests.repository;

import com.kamilla.deppplom.tests.repository.model.TestEntity;
import com.kamilla.deppplom.tests.repository.model.TestVersionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestRepository extends JpaRepository<TestEntity, Integer> {

    Optional<TestEntity> findByTitleAndDisciplineId(String title, int disciplineId);

    List<TestEntity> findByDisciplineId(int id);

    Optional <TestEntity> findByTitleLike(String description);

}