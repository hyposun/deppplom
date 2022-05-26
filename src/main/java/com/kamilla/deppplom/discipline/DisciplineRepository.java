package com.kamilla.deppplom.discipline;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DisciplineRepository extends PagingAndSortingRepository<Discipline, Integer> {

}
