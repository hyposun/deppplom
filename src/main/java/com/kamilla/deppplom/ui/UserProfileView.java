package com.kamilla.deppplom.ui;

import com.kamilla.deppplom.groups.StudentGroup;
import com.kamilla.deppplom.security.SecurityService;
import com.kamilla.deppplom.users.User;
import com.kamilla.deppplom.users.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.listbox.ListBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.RolesAllowed;

@RolesAllowed({"ADMIN", "TEACHER", "STUDENT"})
@PageTitle("Deppplom | Профиль")
@Route(value = "/profile/:userId", layout = BaseLayout.class)
public class UserProfileView extends VerticalLayout implements BeforeEnterObserver {

    private UserService userService;
    private User user;

    private Binder<User> binder = new Binder<>(User.class);
    private IntegerField id = new IntegerField("ID");
    private TextField name = new TextField("Имя");
    private TextField login = new TextField("Логин");
    private TextField roleField = new TextField("Роль");
    private ListBox<StudentGroup> userGroups = new ListBox<>();

    private Button logoutButton = new Button("Выход");

    public UserProfileView(UserService userService, SecurityService securityService) {

        super();
        this.userService = userService;
        binder.bindInstanceFields(this);

        id.setWidthFull();
        id.setReadOnly(true);

        name.setWidthFull();
        name.setReadOnly(true);

        login.setWidthFull();
        login.setReadOnly(true);

        roleField.setWidthFull();
        roleField.setReadOnly(true);

        userGroups.setItemLabelGenerator(StudentGroup::getTitle);
        userGroups.setWidthFull();
        userGroups.setReadOnly(true);

        logoutButton.addClickListener(event -> securityService.logout());

        add(id, name, login, roleField, new Span("Группы"), userGroups, logoutButton);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        var params = event.getRouteParameters();
        user = params.get("userId")
            .map(Integer::parseInt)
            .flatMap(userId -> userService.findById(userId))
            .orElseThrow();
        binder.setBean(user);
        roleField.setValue(user.getRole().getTitle());
        userGroups.setItems(user.getGroups());
    }

}
