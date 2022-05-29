package com.kamilla.deppplom.groups;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentGroupRepository extends JpaRepository<StudentGroup, Integer> {

    Optional<StudentGroup> findByTitle(String title);

    List<StudentGroup> findAllByTitleLike(String title);

}
