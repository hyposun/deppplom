package com.kamilla.deppplom.discipline.repository;

import com.kamilla.deppplom.discipline.Discipline;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DisciplineRepository extends PagingAndSortingRepository<Discipline, Integer> {

    Optional<Discipline> findByTitle(String title);

    Page<Discipline> findAllByTitleLike(String title, Pageable pageable);

}
