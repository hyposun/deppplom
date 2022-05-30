package com.kamilla.deppplom.ui.users;

import com.kamilla.deppplom.groups.StudentGroup;
import com.kamilla.deppplom.groups.StudentGroupService;
import com.kamilla.deppplom.ui.BaseLayout;
import com.kamilla.deppplom.users.Role;
import com.kamilla.deppplom.users.User;
import com.kamilla.deppplom.users.UserRepository;
import com.kamilla.deppplom.users.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import javax.annotation.security.RolesAllowed;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Route(value = "users", layout = BaseLayout.class)
@RolesAllowed("ADMIN")
public class UsersView extends VerticalLayout {

    private UserService service;
    private StudentGroupService studentGroupService;

    private Grid<User> grid = new Grid<>(User.class);
    private TextField nameFilter = new TextField("", "Имя");
    private Select<Role> roleFilter = new Select<>();
    private Select<StudentGroup> groupFilter = new Select<>();
    private Button reset = new Button("Сброс", VaadinIcon.CLOSE.create());
    private Button addNew = new Button("Добавить", VaadinIcon.PLUS.create());
    private HorizontalLayout toolbar = new HorizontalLayout(nameFilter, roleFilter, groupFilter, reset, addNew);

    private UserEditor userEditor;

    @Autowired
    public UsersView(UserService service, StudentGroupService groupService, UserEditor userEditor) {
        this.service = service;
        studentGroupService = groupService;
        this.userEditor = userEditor;
        add(toolbar, grid);
        toolbar.setAlignItems(Alignment.START);
        setupInteractions();
        showUsers();
        setupColumns();
    }

    private void setupInteractions() {

        roleFilter.setPlaceholder("Роль");
        roleFilter.setItemLabelGenerator(Role::getTitle);
        roleFilter.addValueChangeListener(event -> showUsers());
        roleFilter.setItems(Role.values());

        groupFilter.setPlaceholder("Группа");
        groupFilter.setItemLabelGenerator(StudentGroup::getTitle);
        groupFilter.addValueChangeListener(event -> showUsers());
        groupFilter.setItems(studentGroupService.findAll());

        nameFilter.setValueChangeMode(ValueChangeMode.EAGER);
        nameFilter.addValueChangeListener(event -> showUsers());
        nameFilter.setPrefixComponent(VaadinIcon.SEARCH.create());

        grid.asSingleSelect().addValueChangeListener(event -> {
            this.userEditor.editUser(event.getValue());
        });

        userEditor.setChangeHandler(this::showUsers);

        addNew.addClickListener(event -> userEditor.editUser(new User()));

        reset.addClickListener(event -> {
            nameFilter.clear();
            roleFilter.clear();
            groupFilter.clear();
        });

    }

    private void showUsers() {

        var groupId = groupFilter.isEmpty() ? null : groupFilter.getValue().getId();
        var role = roleFilter.isEmpty() ? null : roleFilter.getValue();
        var name = nameFilter.isEmpty() ? null : nameFilter.getValue();

        List<User> users = service.findAll(groupId, role, name);
        grid.setItems(users);
    }

    private void setupColumns() {

        grid.removeAllColumns();

        grid.addColumn(User::getId)
            .setHeader("ID")
            .setResizable(false)
            .setWidth("100px")
            .setFlexGrow(0);

        grid.addColumn(User::getName)
            .setHeader("ФИО");

        grid.addColumn(User::getLogin)
            .setHeader("Логин");

        grid.addColumn((user) -> user.getRole().getTitle())
            .setHeader("Роль");

        grid.addColumn((user) -> {
            return user.getGroups().stream()
                       .map(StudentGroup::getTitle)
                       .collect(Collectors.joining(", "));
        }).setHeader("Группы");

    }

}

