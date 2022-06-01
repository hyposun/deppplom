package com.kamilla.deppplom.group_examination.repository;

import com.kamilla.deppplom.group_examination.repository.model.GroupExaminationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupExaminationRepository extends JpaRepository<GroupExaminationEntity,Integer> {

    Optional <GroupExaminationEntity> findById(int id);

    List<GroupExaminationEntity> findAllByGroupId(int groupId);




}
