package com.kamilla.deppplom.ui.student.myexam;

import com.kamilla.deppplom.examination.model.QuestionExamination;
import com.kamilla.deppplom.examination.model.StudentExamination;
import com.kamilla.deppplom.report.ExaminationReportService;
import com.kamilla.deppplom.report.model.StudentExaminationReport;
import com.kamilla.deppplom.security.SecurityService;
import com.kamilla.deppplom.ui.BaseLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;

import javax.annotation.security.RolesAllowed;

@SpringComponent
@UIScope
@Route(value = "my_examination/:id/status", layout = BaseLayout.class)
@RolesAllowed({"ADMIN", "STUDENT"})
public class StatusMyExaminationComponent extends BaseMyExaminationComponent {

    private Grid<QuestionExamination> questionsGrid = new Grid<>();

    private Span statusField = new Span();
    private TextField minimumPoints = new TextField("Пропускной балл");
    private TextField actualPoints = new TextField("Получено баллов");
    private HorizontalLayout horizontalLayout = new HorizontalLayout();

    public StatusMyExaminationComponent(ExaminationReportService reportService, SecurityService securityService) {
        super(reportService, securityService);


        statusField.setWidthFull();
        minimumPoints.setReadOnly(true);
        minimumPoints.setWidthFull();
        actualPoints.setReadOnly(true);
        actualPoints.setWidthFull();


        horizontalLayout.add(new H2("Результат"));
        horizontalLayout.add(statusField);
        horizontalLayout.add(minimumPoints);
        horizontalLayout.add(actualPoints);
        horizontalLayout.setWidthFull();
        horizontalLayout.setAlignItems(Alignment.BASELINE);
        add(horizontalLayout);

        questionsGrid
                .addColumn(it -> it.getQuestion().getTitle())
                .setHeader("Вопрос").setAutoWidth(true);
        questionsGrid
                .addColumn(it -> it.getQuestion().getDifficulty().getTitle())
                .setHeader("Сложность").setAutoWidth(true);
        questionsGrid
                .addColumn(QuestionExamination::getAnswer)
                .setHeader("Ответ").setAutoWidth(true);
        questionsGrid
                .addColumn(it -> it.getQuestion().getResultDescription())
                .setHeader("Пояснение").setAutoWidth(true);
        questionsGrid
                .addColumn(QuestionExamination::getPoints)
                .setHeader("Баллы").setAutoWidth(true);
        add(questionsGrid);

    }

    @Override
    protected void beforeEvent() {
        StudentExamination studentExamination = report.getStudentExamination();
        if (studentExamination != null) {
            questionsGrid.setItems(studentExamination.getResultList());
            actualPoints.setValue(studentExamination.getPoints() + "");
        }
        minimumPoints.setValue(report.getTest().getMinimumPoints() + "");
        mutateStatusField(report);
    }

    public void mutateStatusField(StudentExaminationReport report) {

        var status = report.getStatus();
        statusField.setText(status.getTitle());

        var theme = "badge contrast";
        switch (status) {
            case PLANNED: theme = "badge"; break;
            case FAILED: theme = "badge error"; break;
            case SUCCESSFUL: theme = "badge success"; break;
        }

        ThemeList themes = statusField.getElement().getThemeList();
        themes.removeIf(it -> it.startsWith("badge"));
        themes.add(theme);
    }

}