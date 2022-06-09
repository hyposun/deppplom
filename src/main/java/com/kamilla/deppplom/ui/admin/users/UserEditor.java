package com.kamilla.deppplom.ui.admin.users;

import com.kamilla.deppplom.groups.StudentGroup;
import com.kamilla.deppplom.groups.StudentGroupRepository;
import com.kamilla.deppplom.users.Role;
import com.kamilla.deppplom.users.User;
import com.kamilla.deppplom.users.UserService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.listbox.MultiSelectListBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.server.ResponseStatusException;

import static com.kamilla.deppplom.ui.utils.UIUtils.successNotification;

@SpringComponent
@UIScope
public class UserEditor extends Dialog implements KeyNotifier {

    private UserService userService;
    private StudentGroupRepository groupRepository;
    private User user;

    private TextField name = new TextField("Имя");
    private TextField login = new TextField("Логин");
    private PasswordField password = new PasswordField("Пароль");
    private Select<Role> role = new Select<>(Role.values());

    private MultiSelectListBox<StudentGroup> groups = new MultiSelectListBox<>();

    private Button save = new Button("Сохранить", VaadinIcon.CHECK.create());
    private Button delete = new Button("Удалить");
    private Button cancel = new Button("Отмена");
    private HorizontalLayout actions = new HorizontalLayout(save, delete, cancel);

    private Binder<User> binder = new Binder<>(User.class);

    @Setter
    private ChangeHandler changeHandler;

    public interface ChangeHandler {
        void onChange();
    }

    @Autowired
    public UserEditor(UserService service, StudentGroupRepository repository) {
        userService = service;
        groupRepository = repository;
        setupLayout();
        setupInteractivity();
        setVisible(false);
    }

    private void setupInteractivity() {

        groups.setItems(groupRepository.findAll());
        groups.setItemLabelGenerator(StudentGroup::getTitle);

        role.setItemLabelGenerator(Role::getTitle);

        binder.bindInstanceFields(this);

        save.getElement().getThemeList().add("primary");

        addKeyPressListener(Key.ENTER, event -> save());
        save.addClickListener(event -> save());
        cancel.addClickListener(event -> close());
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        delete.addClickListener(event -> delete());
    }

    private void setupLayout() {
        FormLayout layout = new FormLayout();
        layout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 2));
        layout.add(name, 1);
        layout.add(login, 1);
        layout.add(password, 1);
        layout.add(role, 1);
        layout.add(groups, 2);
        layout.setVisible(true);
        add(layout, actions);
    }

    private void delete() {
        userService.delete(user.getId());
        changeHandler.onChange();
        setVisible(false);
        successNotification("Пользователь '" + user.getName() + "' удален", 2);
    }

    private void save() {
        try {
            userService.update(user);
            changeHandler.onChange();
            setVisible(false);
            successNotification("Пользователь добавлен", 2);
        } catch (Exception e) {
            var message = e.getMessage();
            if (e instanceof ResponseStatusException) {
                message = ((ResponseStatusException) e).getReason();
            }
            Notification notification = Notification.show(message == null ? e.toString() : message);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.setPosition(Notification.Position.MIDDLE);
        }
    }

    public void editUser(User editedUser) {
        if (editedUser == null) {
            setVisible(false);
            return;
        }

        if (editedUser.getId() > 0) {
            user = userService.findById(editedUser.getId()).orElse(editedUser);
        } else {
            user = new User();
        }
        binder.setBean(user);

        name.focus();
        setVisible(true);
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) open();
        else close();
    }
}
