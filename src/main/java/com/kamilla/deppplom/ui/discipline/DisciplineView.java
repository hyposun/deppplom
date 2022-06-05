package com.kamilla.deppplom.ui.discipline;

import com.kamilla.deppplom.discipline.Discipline;
import com.kamilla.deppplom.discipline.DisciplineService;
import com.kamilla.deppplom.ui.BaseLayout;
import com.kamilla.deppplom.ui.utils.RecursiveTreeGrid;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.RolesAllowed;
import java.util.List;

import static com.kamilla.deppplom.ui.utils.UIUtils.successNotification;

@PageTitle("Deppplom | Дисциплины")
@Route(value = "disciplines", layout = BaseLayout.class)
@RolesAllowed({"ADMIN", "TEACHER"})
public class DisciplineView extends VerticalLayout {

    private DisciplineService service;
    private DisciplineEditor editor;

    private TreeGrid<Discipline> disciplineGrid = new RecursiveTreeGrid<>();

    public DisciplineView(DisciplineService service, DisciplineEditor editor) {
        this.service = service;
        this.editor = editor;

        disciplineGrid.addHierarchyColumn(Discipline::getId).setHeader("ID").setAutoWidth(true);
        disciplineGrid.addColumn(Discipline::getTitle).setHeader("Название").setAutoWidth(true);
        setHeightFull();

        List<Discipline> parents = refreshItems();

        Button addButton = createBaseAddButton();
        Button expandButton = new Button("Раскрыть всё");
        expandButton.addClickListener(event -> disciplineGrid.expandRecursively(parents, 5));
        Button collapseButton = new Button("Скрыть всё");
        collapseButton.addClickListener(event -> disciplineGrid.collapseRecursively(parents, 5));

        HorizontalLayout toolbar = new HorizontalLayout(addButton, expandButton, collapseButton);
        toolbar.setAlignItems(FlexComponent.Alignment.CENTER);
        toolbar.setHeight("var(--lumo-space-xl)");

        disciplineGrid.addComponentColumn(it ->
            new HorizontalLayout(createEditButton(it), createAddButton(it), createDeleteButton(it))
        ).setAutoWidth(true).setFlexGrow(0);

        add(toolbar, disciplineGrid);
    }

    private Component createDeleteButton(Discipline discipline) {
        Button button = new Button("Удалить", VaadinIcon.TRASH.create());
        button.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        button.addClickListener(event -> {
            service.delete(discipline.getId());
            refreshItems();
            successNotification("Дисциплина удалена", 2);
        });
        return button;
    }

    private List<Discipline> refreshItems() {
        List<Discipline> parents = service.findAllByParentId(0);
        disciplineGrid.setItems(parents, it -> service.findAllByParentId(it.getId()));
        return parents;
    }

    private Button createEditButton(Discipline discipline) {
        Button button = new Button("Редактировать", VaadinIcon.EDIT.create());
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        button.addClickListener(event -> {
            editor.setOnClose(this::refreshItems);
            editor.editItem(discipline);
        });
        return button;
    }

    private Button createAddButton(Discipline discipline) {
        Button button = new Button("Добавить", VaadinIcon.PLUS.create());
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
        button.addClickListener(event -> {
            Discipline child = new Discipline();
            child.setTitle("Новая дисциплина");
            child.setParentId(discipline.getId());
            editor.setOnClose(this::refreshItems);
            editor.editItem(child);
        });
        return button;
    }

    private Button createBaseAddButton() {
        Button button = new Button("Добавить", VaadinIcon.PLUS.create());
        button.addClickListener(event -> {
            Discipline child = new Discipline();
            child.setTitle("Новая дисциплина");
            child.setParentId(0);
            editor.setOnClose(this::refreshItems);
            editor.editItem(child);
        });
        return button;
    }

}