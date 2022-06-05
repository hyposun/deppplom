package com.kamilla.deppplom.discipline.repository;

import com.kamilla.deppplom.discipline.Discipline;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DisciplineRepository extends JpaRepository<Discipline, Integer> {

    Optional<Discipline> findByTitle(String title);

    Page<Discipline> findAllByTitleLike(String title, Pageable pageable);

    List<Discipline> findAllByTitleLike(String title);

    List<Discipline> findAllByParentId(int parentId);

    Optional <Discipline> findByTitleAndParentId(String title, int parentId);

}
