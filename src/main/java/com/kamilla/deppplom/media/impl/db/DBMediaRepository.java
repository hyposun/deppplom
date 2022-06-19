package com.kamilla.deppplom.media.impl.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DBMediaRepository extends JpaRepository<DBMediaEntity, Integer> {

    List<DBMediaEntity> findAllByKey(String key);

}
