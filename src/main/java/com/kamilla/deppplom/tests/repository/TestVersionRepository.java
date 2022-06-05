package com.kamilla.deppplom.tests.repository;

import com.kamilla.deppplom.tests.repository.model.TestVersionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestVersionRepository extends JpaRepository<TestVersionEntity, Integer> {

    List<TestVersionEntity> findByTestId(int testId);
}
