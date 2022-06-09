package com.kamilla.deppplom.ui.student.myexam.service;

import com.kamilla.deppplom.examination.StudentExaminationService;
import com.kamilla.deppplom.examination.model.StudentExamination;
import com.kamilla.deppplom.question.model.Question;
import com.kamilla.deppplom.security.SecurityService;
import com.kamilla.deppplom.ui.student.myexam.ClosedQuestionInputComponent;
import com.kamilla.deppplom.ui.student.myexam.OpenedQuestionInputComponent;
import com.kamilla.deppplom.ui.student.myexam.StatusMyExaminationComponent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.RouteParam;
import com.vaadin.flow.router.RouteParameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static java.lang.String.valueOf;

@Component
public class MyExaminationDispatcher {

    @Autowired
    private SecurityService securityService;

    @Autowired
    private ExaminationReportService reportService;

    @Autowired
    private StudentExaminationService studentExaminationService;

    public void dispatch(int groupExaminationId, UI ui) {

        var user = securityService.getAuthenticatedUser().orElseThrow();
        var report = reportService.getStudentExamination(user.getId(), groupExaminationId);
        var status = report.getStatus();

        if (status == ExaminationStatus.PLANNED) return;

        if (status.isActive()) {
            var studentExamination = Optional.ofNullable(report.getStudentExamination())
                    .orElse(studentExaminationService.startExamination(user.getId(), groupExaminationId));
            studentExaminationService
                    .getNextQuestion(studentExamination.getId())
                    .ifPresentOrElse(
                        question -> dispatchQuestion(studentExamination, question, ui),
                        () -> navigateToStatusPage(groupExaminationId, ui)
                    );
        } else {
            navigateToStatusPage(groupExaminationId, ui);
        }

    }

    private void dispatchQuestion(StudentExamination studentExamination, Question question, UI ui) {
        Class<? extends com.vaadin.flow.component.Component> component;
        switch (question.getType()) {
            case OPENED: component = OpenedQuestionInputComponent.class; break;
            case CLOSED: component = ClosedQuestionInputComponent.class; break;
            case CLOSED_ORDERED:
            default:
                throw new IllegalStateException("Неизвестный тип вопрос: " + question.getType());
        }
        RouteParam examinationId = new RouteParam("examinationId", valueOf(studentExamination.getId()));
        RouteParam questionId = new RouteParam("questionId", valueOf(question.getId()));
        ui.navigate(component, new RouteParameters(examinationId, questionId));
    }

    private void navigateToStatusPage(int groupExaminationId, UI ui) {
        var params = new RouteParameters("id", valueOf(groupExaminationId));
        ui.navigate(StatusMyExaminationComponent.class, params);
    }

}
