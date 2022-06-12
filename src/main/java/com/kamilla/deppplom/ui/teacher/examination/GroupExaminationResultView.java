package com.kamilla.deppplom.ui.teacher.examination;

import com.kamilla.deppplom.examination.model.StudentExamination;
import com.kamilla.deppplom.report.ExaminationReportService;
import com.kamilla.deppplom.report.model.GroupExaminationReport;
import com.kamilla.deppplom.report.model.StudentExaminationReport;
import com.kamilla.deppplom.ui.BaseLayout;
import com.kamilla.deppplom.ui.student.myexam.MyExaminationsView;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.apache.commons.lang3.math.NumberUtils;

import javax.annotation.security.RolesAllowed;

import static com.kamilla.deppplom.ui.utils.UIUtils.formatDate;
import static java.lang.String.valueOf;
import static java.util.Optional.ofNullable;

@UIScope
@SpringComponent
@Route(value = "examinations/:id/result", layout = BaseLayout.class)
@RolesAllowed({"ADMIN", "TEACHER"})
public class GroupExaminationResultView extends VerticalLayout implements BeforeEnterObserver, BeforeLeaveObserver {

    private StudentExaminationDialog studentExaminationDialog;
    private ExaminationReportService reportService;
    private GroupExaminationReport report;

    private TextField testField = new TextField("Тест");
    private TextField periodField = new TextField("Период");
    private TextField groupField = new TextField("Группа");
    private TextField disciplineField = new TextField("Дисциплина");

    private TextField minimumPointsField = new TextField("Проходной балл");
    private TextField averagePointsField = new TextField("Средний балл");

    private Grid<StudentExaminationReport> studentReportsGrid = new Grid<>();

    public GroupExaminationResultView(StudentExaminationDialog studentExaminationDialog, ExaminationReportService reportService) {
        this.studentExaminationDialog = studentExaminationDialog;
        this.reportService = reportService;
        this.setHeightFull();

        FormLayout baseInfoLayout = new FormLayout();
        add(baseInfoLayout);

        testField.setReadOnly(true);
        baseInfoLayout.add(testField, 1);
        periodField.setReadOnly(true);
        baseInfoLayout.add(periodField, 1);

        groupField.setReadOnly(true);
        baseInfoLayout.add(groupField, 1);
        disciplineField.setReadOnly(true);
        baseInfoLayout.add(disciplineField, 1);

        FormLayout resultLayout = new FormLayout();
        add(new H3("Результаты"));
        add(resultLayout);
        minimumPointsField.setReadOnly(true);
        resultLayout.add(minimumPointsField, 1);
        averagePointsField.setReadOnly(true);
        resultLayout.add(averagePointsField, 1);

        studentReportsGrid
                .addComponentColumn(MyExaminationsView::getStatusBadge)
                .setHeader("Статус");
        studentReportsGrid
                .addColumn(it -> it.getStudent().getName())
                .setHeader("Студент")
                .setAutoWidth(true);
        studentReportsGrid
                .addColumn(it -> ofNullable(it.getStudentExamination()).map(StudentExamination::getFinished).map(date -> formatDate(date.toLocalDateTime())).orElse("-"))
                .setHeader("Время")
                .setAutoWidth(true);
        studentReportsGrid
                .addColumn(it -> ofNullable(it.getStudentExamination()).map(StudentExamination::getPoints).orElse(0f))
                .setHeader("Получено баллов");

        studentReportsGrid.asSingleSelect()
                        .addValueChangeListener(event -> {
                            var value = event.getValue();
                            if (value == null) return;
                            studentExaminationDialog.show(value);
                        });

        add(studentReportsGrid);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        RouteParameters parameters = event.getRouteParameters();
        report = parameters.get("id")
                    .map(NumberUtils::toInt)
                    .map(it -> reportService.findGroupExamination(it))
                    .orElseThrow();

        testField.setValue(report.getTest().getTitle());
        periodField.setValue(formatDate(report.getFrom()) + " - " +  formatDate(report.getTo()));
        groupField.setValue(report.getGroup().getTitle());
        disciplineField.setValue(report.getDiscipline().getTitle());

        minimumPointsField.setValue(valueOf(report.getTest().getMinimumPoints()));
        averagePointsField.setValue(valueOf(report.getAveragePoints()));

        studentReportsGrid.setItems(report.getReports());
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {

    }
}
