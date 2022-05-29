package com.kamilla.deppplom.ui.groups;

import com.kamilla.deppplom.groups.StudentGroup;
import com.kamilla.deppplom.groups.StudentGroupService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.Setter;
import org.springframework.web.server.ResponseStatusException;

import static com.kamilla.deppplom.ui.UIUtils.successNotification;

@SpringComponent
@UIScope
public class GroupEditor extends Dialog implements KeyNotifier {

    private StudentGroupService service;
    private StudentGroup group;

    @Setter
    private Runnable onClose;

    private Binder<StudentGroup> binder = new Binder<>(StudentGroup.class);
    private TextField title = new TextField("Название");
    private Button save = new Button("Сохранить", VaadinIcon.CHECK.create());
    private Button cancel = new Button("Отмена");
    private HorizontalLayout actions = new HorizontalLayout(save, cancel);

    public GroupEditor(StudentGroupService service) {

        this.service = service;

        VerticalLayout layout = new VerticalLayout(title, actions);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        add(layout);
        setVisible(false);

        binder.bindInstanceFields(this);

        addKeyPressListener(Key.ENTER, event -> save());
        save.getElement().getThemeList().add("primary");

        save.addClickListener(event -> save());
        cancel.addClickListener(event -> setVisible(false));

    }

    public void editGroup(StudentGroup value) {

        if (value == null) {
            setVisible(false);
            return;
        }

        if (value.getId() > 0) {
            this.group = service.findById(value.getId()).orElse(value);
        } else {
            this.group = value;
        }

        binder.setBean(this.group);
        title.focus();
        setVisible(true);
    }

    private void save() {
        try {
            service.update(group);
            onClose.run();
            setVisible(false);
            successNotification("Группа добавлена", 2);
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

    @Override
    public void setVisible(boolean visible) {
        if (visible) open();
        else close();
    }
}
