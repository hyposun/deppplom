package com.kamilla.deppplom.report;

import com.kamilla.deppplom.examination.StudentExaminationService;
import com.kamilla.deppplom.examination.model.StudentExamination;
import com.kamilla.deppplom.group_examination.GroupExamination;
import com.kamilla.deppplom.group_examination.service.GroupExaminationService;
import com.kamilla.deppplom.groups.StudentGroup;
import com.kamilla.deppplom.groups.StudentGroupService;
import com.kamilla.deppplom.report.model.GroupExaminationReport;
import com.kamilla.deppplom.report.model.GroupExaminationStatus;
import com.kamilla.deppplom.report.model.StudentExaminationReport;
import com.kamilla.deppplom.report.model.StudentExaminationStatus;
import com.kamilla.deppplom.users.Role;
import com.kamilla.deppplom.users.User;
import com.kamilla.deppplom.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.kamilla.deppplom.report.model.StudentExaminationStatus.*;
import static java.time.LocalDateTime.now;

@Service
public class ExaminationReportService {

    @Autowired
    private GroupExaminationService groupExaminationService;

    @Autowired
    private StudentExaminationService studentExaminationService;

    @Autowired
    private UserService userService;

    @Autowired
    private StudentGroupService groupService;

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

    public GroupExaminationReport findGroupExamination(int groupExaminationId) {
        var exam = groupExaminationService.findById(groupExaminationId).orElseThrow();
        var users = userService.findAllByRoleAndGroup(exam.getGroup().getId(), Role.STUDENT);
        return getGroupExaminationReport(exam, users);
    }

    public List<GroupExaminationReport> findAllGroupExaminations() {
        return groupService.findAll().stream()
                .flatMap(it -> findAllGroupExaminations(it.getId()).stream())
                .collect(Collectors.toList());
    }

    public List<GroupExaminationReport> findAllGroupExaminations(int groupId) {

        var examinations = groupExaminationService.findByGroupId(groupId);
        var students = userService.findAllByRoleAndGroup(groupId, Role.STUDENT);

        return examinations.stream()
                .map(it -> getGroupExaminationReport(it, students))
                .collect(Collectors.toList());
    }

    private GroupExaminationReport getGroupExaminationReport(GroupExamination examination, List<User> students) {
        GroupExaminationReport report = new GroupExaminationReport();
        report.setId(examination.getId());
        report.setGroup(examination.getGroup());
        report.setDiscipline(examination.getTest().getDiscipline());
        report.setTest(examination.getTest());
        report.setTeacher(examination.getTeacher());
        report.setFrom(examination.getOpenExamTime());
        report.setTo(examination.getCloseExamTime());

        List<StudentExaminationReport> studentReports = students.stream()
                .map(student -> getStudentExamination(student.getId(), examination.getId()))
                .collect(Collectors.toList());
        var finishedStudents = studentReports.stream().filter(it -> !it.getStatus().isActive()).collect(Collectors.toList());
        var average = finishedStudents.stream()
                .filter(it -> it.getStudentExamination() != null)
                .mapToDouble(it -> it.getStudentExamination().getPoints())
                .average().orElse(0);

        report.setReports(studentReports);
        report.setFinishedStudentsQuantity(finishedStudents.size());
        report.setAveragePoints((float) average);

        if (examination.getCloseExamTime().isBefore(now())) {
            report.setStatus(GroupExaminationStatus.FINISHED);
        } else {
            if (examination.getOpenExamTime().isAfter(now())) {
                report.setStatus(GroupExaminationStatus.PLANNED);
            } else {
                report.setStatus(GroupExaminationStatus.IN_PROCESS);
            }
        }

        return report;
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


    private StudentExaminationStatus getStatus(GroupExamination groupExamination, StudentExamination studentExamination) {

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