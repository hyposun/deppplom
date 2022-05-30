package com.kamilla.deppplom.ui.discipline;

import com.kamilla.deppplom.discipline.Discipline;
import com.kamilla.deppplom.discipline.DisciplineService;
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

import static com.kamilla.deppplom.ui.utils.UIUtils.successNotification;

@SpringComponent
@UIScope
public class DisciplineEditor extends Dialog implements KeyNotifier {

    private DisciplineService service;
    private Discipline discipline;

    @Setter
    private Runnable onClose;

    private Binder<Discipline> binder = new Binder<>(Discipline.class);
    private TextField title = new TextField("Название");
    private Button save = new Button("Сохранить", VaadinIcon.CHECK.create());
    private Button cancel = new Button("Отмена");
    private HorizontalLayout actions = new HorizontalLayout(save, cancel);


    public DisciplineEditor(DisciplineService service) {

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

    private void save() {
        try {
            service.update(discipline);
            onClose.run();
            setVisible(false);
            successNotification("Дисциплина добавлена", 2);
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

    public void editItem(Discipline value) {
        if (value == null) {
            setVisible(false);
            return;
        }

        discipline = service.findById(value.getId()).orElse(value);
        binder.setBean(discipline);
        title.focus();
        setVisible(true);
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) open();
        else close();
    }

}

