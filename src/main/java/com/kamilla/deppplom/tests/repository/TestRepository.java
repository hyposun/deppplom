package com.kamilla.deppplom.tests.repository;

import com.kamilla.deppplom.tests.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestRepository extends JpaRepository<Test,Integer> {

    List<Test> findByDisciplineId(int id);

    Optional <Test> findByTitleLike(String description);

}
