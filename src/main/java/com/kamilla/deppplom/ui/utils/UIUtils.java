package com.kamilla.deppplom.ui.utils;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_TIME;

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

    public static String formatDate(LocalDateTime time) {
        return time.format(ISO_LOCAL_DATE) + " " + time.format(ISO_LOCAL_TIME);
    }

}
