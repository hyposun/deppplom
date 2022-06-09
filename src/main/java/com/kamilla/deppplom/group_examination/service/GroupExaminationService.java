package com.kamilla.deppplom.group_examination.service;

import com.kamilla.deppplom.group_examination.GroupExamination;
import com.kamilla.deppplom.group_examination.repository.GroupExaminationRepository;
import com.kamilla.deppplom.group_examination.repository.model.GroupExaminationEntity;
import com.kamilla.deppplom.group_examination.service.model.GroupExaminationRequest;
import com.kamilla.deppplom.groups.StudentGroup;
import com.kamilla.deppplom.groups.StudentGroupService;
import com.kamilla.deppplom.tests.TestService;
import com.kamilla.deppplom.tests.model.Test;
import com.kamilla.deppplom.users.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class GroupExaminationService {

    @Autowired
    StudentGroupService groupService;

    @Autowired
    TestService testService;

    @Autowired
    GroupExaminationRepository repository;

    public GroupExamination startGroupExamination(GroupExaminationRequest request){
        StudentGroup group = getGroup(request.getGroupId());
        Test test = getTest(request.getTestId());

        var groupExaminationEntity = new GroupExaminationEntity();
        groupExaminationEntity.setGroupId(request.getGroupId());
        groupExaminationEntity.setTestId(request.getTestId());
        groupExaminationEntity.setCloseExamTime(request.getCloseExamTime());
        groupExaminationEntity.setOpenExamTime(request.getOpenExamTime());

        User teacher = new User();
        teacher.setId(request.getTeacherId());
        groupExaminationEntity.setTeacher(teacher);

        groupExaminationEntity = repository.save(groupExaminationEntity);

        return getGroupModel(groupExaminationEntity, group, test);
    }

    private GroupExamination getGroupModel(GroupExaminationEntity entity) {
        StudentGroup group = getGroup(entity.getGroupId());
        Test test = getTest(entity.getTestId());
        return getGroupModel(entity, group, test);
    }

    private GroupExamination getGroupModel(GroupExaminationEntity entity, StudentGroup group, Test test){
        return new GroupExamination(entity.getId(),
            test,
            group,
            entity.getOpenExamTime(),
            entity.getCloseExamTime(),
            entity.getTeacher()
        );
    }

    public Optional<GroupExamination> findById(int groupExamId) {
        return repository.findById(groupExamId).map(this::getGroupModel);
    }

    public List<GroupExamination> findByGroupId(int groupId) {
        var entities = repository.findAllByGroupId(groupId);
        return entities.stream().map(this::getGroupModel).collect(Collectors.toList());
    }

    public List<GroupExamination> findAll() {
        return repository
                .findAll().stream()
                .map(this::getGroupModel)
                .collect(Collectors.toList());
    }

    private StudentGroup getGroup(int groupId) {
       return groupService.findById(groupId).orElseThrow(() -> new IllegalArgumentException("Данная группа не найдена"));
    }

    private Test getTest(int testId) {
        return testService.findById(testId).orElseThrow(()-> new IllegalArgumentException("Данный тест не найден"));
    }


}
