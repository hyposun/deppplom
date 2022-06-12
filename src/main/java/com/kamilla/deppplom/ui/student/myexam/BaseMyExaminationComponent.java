package com.kamilla.deppplom.ui.student.myexam;

import com.kamilla.deppplom.report.ExaminationReportService;
import com.kamilla.deppplom.report.model.StudentExaminationReport;
import com.kamilla.deppplom.security.SecurityService;
import com.kamilla.deppplom.users.User;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;

import static com.kamilla.deppplom.ui.utils.UIUtils.formatDate;

public abstract class BaseMyExaminationComponent extends VerticalLayout implements BeforeEnterObserver, BeforeLeaveObserver {

    protected ExaminationReportService reportService;
    protected SecurityService securityService;

    protected User user;
    protected StudentExaminationReport report;

    private TextField testNameField = new TextField("Тест");
    private TextField teacherField = new TextField("Преподаватель");
    private TextField dateTimeField = new TextField("Дата");

    public BaseMyExaminationComponent(ExaminationReportService reportService, SecurityService securityService) {
        this.reportService = reportService;
        this.securityService = securityService;
        setHeightFull();

        testNameField.setReadOnly(true);
        testNameField.setWidthFull();
        dateTimeField.setReadOnly(true);
        dateTimeField.setWidthFull();
        teacherField.setReadOnly(true);
        teacherField.setWidthFull();

        HorizontalLayout formLayout = new HorizontalLayout();
        formLayout.add(testNameField, dateTimeField, testNameField);
        formLayout.setWidthFull();

        add(formLayout);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {

        RouteParameters parameters = event.getRouteParameters();
        var id = parameters.get("id").map(Integer::parseInt).orElse(0);
        user = securityService.getAuthenticatedUser().orElseThrow();
        report = reportService.getStudentExamination(user.getId(), id);

        testNameField.setValue(report.getTest().getTitle());
        dateTimeField.setValue(formatDate(report.getFrom()));
        teacherField.setValue(report.getTeacher().getName());

        beforeEvent();
    }

    protected abstract void beforeEvent();

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {

    }
}
