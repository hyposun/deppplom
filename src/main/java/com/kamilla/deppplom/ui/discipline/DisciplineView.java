package com.kamilla.deppplom.ui.discipline;

import com.kamilla.deppplom.discipline.Discipline;
import com.kamilla.deppplom.discipline.DisciplineService;
import com.kamilla.deppplom.ui.BaseLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.RolesAllowed;

@PageTitle("Deppplom | Дисциплины")
@Route(value = "disciplines", layout = BaseLayout.class)
@RolesAllowed({"ADMIN", "TEACHER"})
public class DisciplineView extends VerticalLayout {

    private DisciplineService service;
    private DisciplineEditor editor;

    private Grid<Discipline> grid = new Grid<>(Discipline.class);
    private TextField filter = new TextField("", "Название");
    private Button addNew = new Button("Добавить", VaadinIcon.PLUS.create());
    private HorizontalLayout toolbar = new HorizontalLayout(filter, addNew);

    public DisciplineView(DisciplineService service, DisciplineEditor editor) {
        this.service = service;
        this.editor = editor;
        add(toolbar, grid);
        setupInteractivity();
        showItems("");
    }

    private void setupInteractivity() {
        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(event -> showItems(event.getValue()));

        grid.asSingleSelect()
            .addValueChangeListener(event -> editor.editItem(event.getValue()));

        editor.setOnClose(() -> showItems(filter.getValue()));
        addNew.addClickListener(event -> editor.editItem(new Discipline()));
    }

    private void showItems(String titleLike) {
        if (titleLike == null || titleLike.isBlank()) {
            grid.setItems(service.findAll());
        } else {
            grid.setItems(service.findAllByTitleLike(titleLike));
        }
        grid.removeAllColumns();
        grid.addColumn(Discipline::getId)
            .setHeader("ID")
            .setWidth("50px")
            .setFlexGrow(0)
            .setAutoWidth(false);
        grid.addColumn(Discipline::getTitle)
            .setHeader("Название");
    }
}

