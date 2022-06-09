package com.kamilla.deppplom.ui.teacher.examination;

import com.kamilla.deppplom.group_examination.GroupExamination;
import com.kamilla.deppplom.group_examination.service.GroupExaminationService;
import com.kamilla.deppplom.ui.BaseLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;

import javax.annotation.security.RolesAllowed;
import java.util.List;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

@PageTitle("Deppplom | Дисциплины")
@Route(value = "examinations", layout = BaseLayout.class)
@RolesAllowed({"ADMIN", "TEACHER"})
public class GroupExaminationView extends VerticalLayout {

    private GroupExaminationService service;

    private Grid<GroupExamination> examinationGrid = new Grid<>();

    public GroupExaminationView(GroupExaminationService service) {
        this.service = service;

        setupGridColumns();

        Button addButton = new Button("Добавить", VaadinIcon.PLUS.create());
        addButton.addClickListener(event -> openEditor(0));

        setHeightFull();
        add(new HorizontalLayout(addButton), examinationGrid);
        refreshGrid();
    }

    private void setupGridColumns() {
        examinationGrid.addColumn(GroupExamination::getId)
                .setHeader("ID")
                .setAutoWidth(true);
        examinationGrid.addColumn(this::getDates)
                .setHeader("Период")
                .setAutoWidth(true);
        examinationGrid.addColumn(it -> it.getGroup().getTitle())
                .setHeader("Группа")
                .setAutoWidth(true);
        examinationGrid.addColumn(it -> it.getTest().getTitle())
                        .setHeader("Тест")
                        .setAutoWidth(true);
    }

    private String getDates(GroupExamination exam) {
        return exam.getOpenExamTime().format(ISO_LOCAL_DATE)
                + " - "
                + exam.getCloseExamTime().format(ISO_LOCAL_DATE);
    }

    private void refreshGrid() {
        List<GroupExamination> examinations = service.findAll();
        examinationGrid.setItems(examinations);
    }

    private void openEditor(int id) {
        getUI().ifPresent(ui -> {
            var params = new RouteParameters("id", String.valueOf(id));
            ui.navigate(GroupExaminationEditor.class, params);
        });
    }

}
