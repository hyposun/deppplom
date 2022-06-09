package com.kamilla.deppplom.ui.student.myexam;

import com.kamilla.deppplom.security.SecurityService;
import com.kamilla.deppplom.ui.BaseLayout;
import com.kamilla.deppplom.ui.student.myexam.service.ExaminationReportService;
import com.kamilla.deppplom.ui.student.myexam.service.MyExaminationDispatcher;
import com.kamilla.deppplom.ui.student.myexam.service.StudentExaminationReport;
import com.kamilla.deppplom.users.User;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.RolesAllowed;

import static com.kamilla.deppplom.ui.utils.UIUtils.formatDate;
import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;

@PageTitle("Deppplom | Мои экзамены")
@Route(value = "my_examinations", layout = BaseLayout.class)
@RolesAllowed({"ADMIN", "STUDENT"})
public class MyExaminationsView extends VerticalLayout {

    private ExaminationReportService service;
    private SecurityService securityService;
    private MyExaminationDispatcher dispatcher;

    private Checkbox showOnlyActual = new Checkbox("Показывать только актуальные", true);
    private Grid<StudentExaminationReport> examinationsGrid = new Grid<>();

    public MyExaminationsView(ExaminationReportService service, SecurityService securityService, MyExaminationDispatcher dispatcher) {
        super();
        this.service = service;
        this.securityService = securityService;
        this.dispatcher = dispatcher;

        setupGrid();
        add(new HorizontalLayout(showOnlyActual));
        add(examinationsGrid);
        setHeightFull();
    }

    private void setupGrid() {

        examinationsGrid
                .addComponentColumn(this::getStatusBadge)
                .setTextAlign(ColumnTextAlign.CENTER)
                .setHeader("Статус").setAutoWidth(true);

        examinationsGrid
                .addColumn(it -> it.getDiscipline().getTitle())
                .setHeader("Дисциплина").setAutoWidth(true);

        examinationsGrid
                .addColumn(it -> it.getTest().getTitle())
                .setHeader("Тест").setAutoWidth(true);

        examinationsGrid
                .addColumn(it -> formatDate(it.getFrom()))
                .setHeader("Начало").setAutoWidth(true)
                .setSortable(true);

        examinationsGrid
                .addColumn(it -> formatDate(it.getTo()))
                .setHeader("Окончание").setAutoWidth(true);

        examinationsGrid
                .addColumn(it -> it.getTeacher().getName())
                .setHeader("Преподаватель").setAutoWidth(true);

        refreshGrid();

        showOnlyActual.addValueChangeListener(event -> refreshGrid());
        examinationsGrid.asSingleSelect()
                .addValueChangeListener(event -> {
                    if (event.getValue() != null) onSelection(event.getValue());
                });
    }

    private void onSelection(StudentExaminationReport value) {
        getUI().ifPresent(ui -> {
            dispatcher.dispatch(value.getGroupExaminationId(), ui);
        });
    }


    private Component getStatusBadge(StudentExaminationReport report) {
        var status = report.getStatus();
        Span badge = new Span(status.getTitle());

        var theme = "badge contrast";
        switch (status) {
            case PLANNED: theme = "badge"; break;
            case FAILED: theme = "badge error"; break;
            case SUCCESSFUL: theme = "badge success"; break;
        }

        badge.getElement().getThemeList().add(theme);
        return badge;
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private void refreshGrid() {

        User user = securityService.getAuthenticatedUser().get();

        var items = service.findAllStudentExaminations(user.getId());
        if (showOnlyActual.getValue()) {
            var now = now();
            items = items.stream()
                    .filter(it -> it.getTo().isAfter(now))
                    .collect(toList());
        }

        examinationsGrid.setItems(items);
    }

}


