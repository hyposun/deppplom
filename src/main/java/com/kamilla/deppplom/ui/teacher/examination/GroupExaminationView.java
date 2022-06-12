package com.kamilla.deppplom.ui.teacher.examination;

import com.kamilla.deppplom.report.ExaminationReportService;
import com.kamilla.deppplom.report.model.GroupExaminationReport;
import com.kamilla.deppplom.security.SecurityService;
import com.kamilla.deppplom.ui.BaseLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;

import javax.annotation.security.RolesAllowed;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.kamilla.deppplom.ui.utils.UIUtils.formatDate;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

@PageTitle("Deppplom | Экзамены")
@Route(value = "examinations", layout = BaseLayout.class)
@RolesAllowed({"ADMIN", "TEACHER"})
public class GroupExaminationView extends VerticalLayout {

    private ExaminationReportService reportService;
    private SecurityService securityService;

    private Checkbox showOnlyMy = new Checkbox("Только свои группы", true);
    private Checkbox showOnlyActual = new Checkbox("Только актуальные", true);
    private Grid<GroupExaminationReport> examinationGrid = new Grid<>();

    public GroupExaminationView(ExaminationReportService reportService, SecurityService securityService) {
        this.reportService = reportService;
        this.securityService = securityService;

        setupGridColumns();

        Button addButton = new Button("Добавить", VaadinIcon.PLUS.create());
        addButton.addClickListener(event -> openEditor(0));

        setHeightFull();
        showOnlyActual.addValueChangeListener(event -> refreshGrid());
        showOnlyMy.addValueChangeListener(event -> refreshGrid());
        HorizontalLayout layout = new HorizontalLayout(showOnlyActual, showOnlyMy, addButton);
        layout.setAlignItems(Alignment.BASELINE);
        add(layout, examinationGrid);
        refreshGrid();
    }

    private void setupGridColumns() {

        examinationGrid
                .addComponentColumn(GroupExaminationView::getStatusBadge)
                .setTextAlign(ColumnTextAlign.CENTER)
                .setHeader("Статус").setAutoWidth(true);

        column("Начало", it -> formatDate(it.getFrom()));
        column("Окончание", it -> formatDate(it.getTo()));
        column("Группа", it -> it.getGroup().getTitle());
        column("Тест", it -> it.getTest().getTitle());
        column("Прогресс", it -> it.getFinishedStudentsQuantity() + " из " + it.getReports().size());
        column("Средний балл", GroupExaminationReport::getAveragePoints);

        examinationGrid.asSingleSelect()
                .addValueChangeListener(event -> {
                    GroupExaminationReport value = event.getValue();
                    if (value == null) return;
                    getUI().ifPresent(ui -> {
                        var params = new RouteParameters("id", String.valueOf(value.getId()));
                        ui.navigate(GroupExaminationResultView.class, params);
                    });
                });

    }

    public static Component getStatusBadge(GroupExaminationReport report) {
        var status = report.getStatus();
        Span badge = new Span(status.getTitle());

        String theme = "";
        switch (status) {
            case PLANNED: theme = "badge"; break;
            case IN_PROCESS: theme = "badge success"; break;
            case FINISHED: theme = "badge contrast"; break;
        }

        badge.getElement().getThemeList().add(theme);
        return badge;
    }

    private void column(String header, ValueProvider<GroupExaminationReport, ?> valueProvider) {
        examinationGrid.addColumn(valueProvider)
                .setHeader(header)
                .setAutoWidth(true);
    }


    private String getDates(GroupExaminationReport exam) {
        return exam.getFrom().format(ISO_LOCAL_DATE)
                + " - "
                + exam.getTo().format(ISO_LOCAL_DATE);
    }

    private void refreshGrid() {

        Stream<GroupExaminationReport> items;
        if (showOnlyMy.getValue()) {
            var user = securityService.getAuthenticatedUser().orElseThrow();
            items = user.getGroups().stream()
                    .flatMap(it -> reportService.findAllGroupExaminations(it.getId()).stream());
        } else {
            items = reportService.findAllGroupExaminations().stream();
        }

        if (showOnlyActual.getValue()) {
            items = items.filter(GroupExaminationReport::isActual);
        }

        examinationGrid.setItems(items.collect(Collectors.toList()));
    }

    private void openEditor(int id) {
        getUI().ifPresent(ui -> {
            var params = new RouteParameters("id", String.valueOf(id));
            ui.navigate(GroupExaminationEditor.class, params);
        });
    }

}
