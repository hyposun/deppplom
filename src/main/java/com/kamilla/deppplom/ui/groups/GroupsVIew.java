package com.kamilla.deppplom.ui.groups;

import com.kamilla.deppplom.groups.StudentGroup;
import com.kamilla.deppplom.groups.StudentGroupRepository;
import com.kamilla.deppplom.ui.BaseLayout;
import com.kamilla.deppplom.users.User;
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

@Route(value = "groups", layout = BaseLayout.class)
@RolesAllowed("ADMIN")
@PageTitle("Deppplom | Группы")
public class GroupsVIew extends VerticalLayout {

    private StudentGroupRepository repository;
    private GroupEditor editor;

    private Grid<StudentGroup> grid = new Grid<>(StudentGroup.class);
    private TextField filter = new TextField("", "Название");
    private Button addNew = new Button("Добавить", VaadinIcon.PLUS.create());
    private HorizontalLayout toolbar = new HorizontalLayout(filter, addNew);

    public GroupsVIew(StudentGroupRepository repository, GroupEditor editor) {
        this.repository = repository;
        this.editor = editor;
        add(toolbar, grid);
        setupInteractivity();
        showGroups("");
    }

    private void setupInteractivity() {

        filter.setValueChangeMode(ValueChangeMode.EAGER);
        filter.addValueChangeListener(event -> showGroups(event.getValue()));
        filter.setPrefixComponent(VaadinIcon.SEARCH.create());

        grid.asSingleSelect()
            .addValueChangeListener(event -> editor.editGroup(event.getValue()));

        editor.setOnClose(() -> showGroups(filter.getValue()));

        addNew.addClickListener(event -> {
            editor.editGroup(new StudentGroup());
        });

    }

    private void showGroups(String name) {
        if (name == null || name.isBlank()) {
            grid.setItems(repository.findAll());
        } else {
            grid.setItems(repository.findAllByTitleLike("%" + name + "%"));
        }
        grid.removeAllColumns();
        grid.addColumn(StudentGroup::getId)
            .setHeader("ID")
            .setWidth("50px")
            .setFlexGrow(0)
            .setAutoWidth(false);
        grid.addColumn(StudentGroup::getTitle)
            .setHeader("Название");
    }

}

