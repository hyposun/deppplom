package com.kamilla.deppplom.ui.utils;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

import java.util.concurrent.TimeUnit;

public class UIUtils {

    public static void successNotification(String message, int seconds) {
        Notification notification = Notification.show(message);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.setPosition(Notification.Position.MIDDLE);
        notification.setDuration((int) TimeUnit.SECONDS.toMillis(seconds));
    }

    public static void errorNotification(String message, int seconds) {
        Notification notification = Notification.show(message);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        notification.setPosition(Notification.Position.MIDDLE);
        notification.setDuration((int) TimeUnit.SECONDS.toMillis(seconds));
    }

}
