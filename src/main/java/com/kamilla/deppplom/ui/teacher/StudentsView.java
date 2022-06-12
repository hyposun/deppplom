package com.kamilla.deppplom.ui.teacher;

import com.kamilla.deppplom.groups.StudentGroup;
import com.kamilla.deppplom.security.SecurityService;
import com.kamilla.deppplom.ui.BaseLayout;
import com.kamilla.deppplom.users.Role;
import com.kamilla.deppplom.users.User;
import com.kamilla.deppplom.users.UserService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.RolesAllowed;
import java.util.Collections;
import java.util.stream.Collectors;

@Route(value = "students", layout = BaseLayout.class)
@RolesAllowed({"ADMIN", "TEACHER"})
@PageTitle("Deppplom | Студенты")
public class StudentsView extends VerticalLayout {

    private SecurityService securityService;
    private UserService userService;
    private User user;

    private Select<StudentGroup> groupFilter = new Select<>();
    private Grid<User> studentsGrid = new Grid<>();

    public StudentsView(SecurityService securityService, UserService userService) {

        this.securityService = securityService;
        this.userService = userService;

        groupFilter.setPlaceholder("Выберите группу");
        groupFilter.setItemLabelGenerator(StudentGroup::getTitle);
        groupFilter.addValueChangeListener(event -> refresh());

        add(new HorizontalLayout(groupFilter));
        add(studentsGrid);

        user = securityService.getAuthenticatedUser().orElseThrow();
        groupFilter.setItems(user.getGroups());

        studentsGrid.setHeightFull();
        setWidthFull();
        setHeightFull();

        studentsGrid
                .addColumn(User::getId)
                .setHeader("ID")
                .setAutoWidth(true);

        studentsGrid
                .addColumn(User::getName)
                .setHeader("Имя")
                .setAutoWidth(true);
        studentsGrid
                .addColumn(it -> it.getGroups().stream().findFirst().orElseThrow().getTitle())
                .setHeader("Группа")
                .setAutoWidth(true);

        refresh();
    }

    private void refresh() {
        var groups = groupFilter.isEmpty() ? user.getGroups() : Collections.singleton(groupFilter.getValue());
        var items = groups.stream()
                .flatMap(group -> userService.findAllByRoleAndGroup(group.getId(), Role.STUDENT).stream())
                .collect(Collectors.toList());
        studentsGrid.setItems(items);
    }

}
