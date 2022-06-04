package com.kamilla.deppplom.ui.test;

import com.kamilla.deppplom.discipline.Discipline;
import com.kamilla.deppplom.discipline.DisciplineService;
import com.kamilla.deppplom.tests.TestService;
import com.kamilla.deppplom.tests.model.Test;
import com.kamilla.deppplom.ui.BaseLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.*;

import javax.annotation.security.RolesAllowed;
import java.util.List;

import static com.kamilla.deppplom.ui.utils.UIUtils.errorNotification;
import static java.util.Collections.emptyList;

@SuppressWarnings("FieldMayBeFinal")
@Route(value = "tests", layout = BaseLayout.class)
@RolesAllowed({"ADMIN", "TEACHER"})
@PageTitle("Deppplom | Тесты")
public class TestsView extends VerticalLayout {

    private TestService testService;
    private Select<Discipline> disciplineSelect;
    private Button addButton;

    private Grid<Test> grid = new Grid<>();

    public TestsView(
            TestService testService,
            DisciplineService disciplineService
    ) {
        this.testService = testService;

        disciplineSelect = new Select<>();
        List<Discipline> disciplines = disciplineService.findAll();
        disciplineSelect.setItems(disciplines);
        if (disciplines.size() > 0) disciplineSelect.setValue(disciplines.get(0));

        disciplineSelect.setItemLabelGenerator(Discipline::getTitle);
        disciplineSelect.addValueChangeListener(event -> refreshItems());

        grid.asSingleSelect()
                .addValueChangeListener(event -> {
                    Test value = event.getValue();
                    if (value != null) navigateToEditor(value.getId());
                });

        grid.removeAllColumns();
        grid.addColumn(Test::getId)
                .setAutoWidth(true)
                .setHeader("ID");
        grid.addColumn(Test::getTitle)
                .setHeader("Название");
        grid.addColumn(Test::getMinimumPoints)
                .setAutoWidth(true)
                .setHeader("Пропускной балл");

        addButton = new Button("Добавить", VaadinIcon.PLUS.create());
        addButton.addClickListener(event -> navigateToEditor(0));

        HorizontalLayout toolbar = new HorizontalLayout(disciplineSelect, addButton);
        add(toolbar, grid);
        setHeightFull();

        refreshItems();
    }

    private void navigateToEditor(int testId) {
        if (disciplineSelect.isEmpty()) {
            errorNotification("Сначала нужно выбрать дисциплину", 2);
            return;
        }
        Discipline discipline = disciplineSelect.getValue();
        getUI().ifPresent(it -> {
            var testIdParam = new RouteParam("testId", String.valueOf(testId));
            var disciplineIdParam = new RouteParam("disciplineId", String.valueOf(discipline.getId()));
            it.navigate(TestEditorView.class, new RouteParameters(disciplineIdParam, testIdParam));
        });
    }

    private void refreshItems() {
        if (disciplineSelect.isEmpty()) {
            grid.setItems(emptyList());
        } else {
            Discipline discipline = disciplineSelect.getValue();
            grid.setItems(testService.findAll(discipline.getId()));
        }
    }

}