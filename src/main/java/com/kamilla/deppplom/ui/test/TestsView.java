package com.kamilla.deppplom.ui.test;

import com.kamilla.deppplom.discipline.Discipline;
import com.kamilla.deppplom.discipline.DisciplineService;
import com.kamilla.deppplom.tests.TestService;
import com.kamilla.deppplom.tests.model.Test;
import com.kamilla.deppplom.ui.BaseLayout;
import com.kamilla.deppplom.ui.utils.UIUtils;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;

import javax.annotation.security.RolesAllowed;

import java.util.List;

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
            TestAddEditor testAddEditor,
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

        testAddEditor.setOnClose(this::refreshItems);
        grid.asSingleSelect()
                .addValueChangeListener(event -> {
                    if (event.getValue() == null) return;
                    getUI().ifPresent(it -> {
                        var params = new RouteParameters("testId", String.valueOf(event.getValue().getId()));
                        it.navigate(TestInformationComponent.class, params);
                    });
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
        addButton.addClickListener(event -> {
            if (disciplineSelect.isEmpty()) {
                UIUtils.errorNotification("Сначала нужно выбрать дисциплину", 2);
                return;
            }
            testAddEditor.start(disciplineSelect.getValue().getId());
        });

        HorizontalLayout toolbar = new HorizontalLayout(disciplineSelect, addButton);
        add(toolbar, grid);
        setHeightFull();

        refreshItems();
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