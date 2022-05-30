package com.kamilla.deppplom.ui.utils;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyNotifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import lombok.Setter;

public abstract class AbstractDialog extends Dialog implements KeyNotifier {

    @Setter
    protected Runnable onClose;

    protected Button save = new Button("Сохранить", VaadinIcon.CHECK.create());
    protected Button delete = new Button("Удалить", VaadinIcon.TRASH.create());
    protected Button cancel = new Button("Отмена");
    protected HorizontalLayout actions = new HorizontalLayout(save, delete, cancel);

    public AbstractDialog() {

        addKeyPressListener(Key.ENTER, event -> save());
        save.getElement().getThemeList().add("primary");
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        actions.setMargin(true);

        save.addClickListener(event -> save());
        delete.addClickListener(event -> delete());
        cancel.addClickListener(event -> {
            onClose.run();
            setVisible(false);
        });

    }

    protected abstract void delete();

    protected abstract void save();

    @Override
    public void setVisible(boolean visible) {
        if (visible) open();
        else close();
    }

}
