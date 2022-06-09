package com.kamilla.deppplom.ui.student.myexam.service;

import com.kamilla.deppplom.examination.StudentExaminationService;
import com.kamilla.deppplom.examination.model.StudentExamination;
import com.kamilla.deppplom.group_examination.GroupExamination;
import com.kamilla.deppplom.group_examination.service.GroupExaminationService;
import com.kamilla.deppplom.groups.StudentGroup;
import com.kamilla.deppplom.users.User;
import com.kamilla.deppplom.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.kamilla.deppplom.ui.student.myexam.service.ExaminationStatus.*;
import static java.time.LocalDateTime.now;

@Service
public class ExaminationReportService {

    @Autowired
    private GroupExaminationService groupExaminationService;

    @Autowired
    private StudentExaminationService studentExaminationService;

    @Autowired
    private UserService userService;

    public StudentExaminationReport getStudentExamination(int studentId, int groupExaminationId) {
        User student = getStudent(studentId);
        GroupExamination groupExamination = groupExaminationService.findById(groupExaminationId).get();
        return getExaminationReport(student, groupExamination);
    }

    public List<StudentExaminationReport> findAllStudentExaminations(int userId) {
        var user = getStudent(userId);
        return user.getGroups().stream()
            .flatMap(group -> getAllGroupReports(user, group))
            .collect(Collectors.toList());
    }

    private User getStudent(int userId) {
        return userService.findById(userId).get();
    }

    private Stream<StudentExaminationReport> getAllGroupReports(User user, StudentGroup group) {
        return groupExaminationService
                .findByGroupId(group.getId()).stream()
                .map(groupExamination -> getExaminationReport(user, groupExamination));
    }

    private StudentExaminationReport getExaminationReport(User user, GroupExamination groupExamination) {
        var report = new StudentExaminationReport();
        report.setGroupExaminationId(groupExamination.getId());
        report.setGroup(groupExamination.getGroup());
        report.setStudent(user);
        report.setTeacher(groupExamination.getTeacher());
        report.setDiscipline(groupExamination.getTest().getDiscipline());
        report.setTest(groupExamination.getTest());
        report.setFrom(groupExamination.getOpenExamTime());
        report.setTo(groupExamination.getCloseExamTime());

        var studentExam = studentExaminationService
                .findByStudentIdAndGroupExaminationId(user.getId(), groupExamination.getId())
                .orElse(null);
        report.setStudentExamination(studentExam);

        var status = getStatus(groupExamination, studentExam);
        report.setStatus(status);
        return report;
    }


    private ExaminationStatus getStatus(GroupExamination groupExamination, StudentExamination studentExamination) {

        if (groupExamination.getOpenExamTime().isAfter(now())) {
            return PLANNED;
        }

        var currentExam = groupExamination.getCloseExamTime().isAfter(now());
        if (studentExamination == null) {
            return currentExam ? NOT_STARTED : FAILED;
        }

        var passed = studentExamination.getPoints() >= groupExamination.getTest().getMinimumPoints();
        if (passed) return SUCCESSFUL;

        if (studentExamination.getFinished() == null) {
            return currentExam ? IN_PROCESS : FAILED;
        } else {
            return FAILED;
        }
    }

}