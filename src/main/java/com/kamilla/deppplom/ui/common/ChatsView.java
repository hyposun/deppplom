package com.kamilla.deppplom.ui.common;

import com.kamilla.deppplom.chat.ChatService;
import com.kamilla.deppplom.chat.model.Chat;
import com.kamilla.deppplom.chat.model.ChatMessage;
import com.kamilla.deppplom.security.SecurityService;
import com.kamilla.deppplom.ui.BaseLayout;
import com.kamilla.deppplom.ui.utils.UIUtils;
import com.kamilla.deppplom.users.User;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;

import javax.annotation.security.RolesAllowed;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@PageTitle("Deppplom | Сообщения")
@Route(value = "chats", layout = BaseLayout.class)
@RolesAllowed({"ADMIN", "STUDENT", "TEACHER"})
public class ChatsView extends VerticalLayout implements BeforeEnterObserver {

    private SecurityService securityService;
    private ChatService chatService;

    private User user;

    private TextField nameFilter = new TextField("Имя");
    private Grid<Chat> chatGrid = new Grid<>();

    public ChatsView(SecurityService securityService, ChatService chatService) {
        this.securityService = securityService;
        this.chatService = chatService;

        chatGrid.setWidthFull();

        chatGrid.addComponentColumn(chat -> {
            Span badge = new Span();
            if (chat.isUpdaterFor(user.getId())) {
                badge.setText("Новое сообщение");
                badge.getElement().getThemeList().add("badge success");
            } else {
                badge.setText("Прочитано");
                badge.getElement().getThemeList().add("badge");
            }
            return badge;
        }).setHeader("Статус").setAutoWidth(true);

        chatGrid.addColumn(chat -> getUserTitle(chat.getOppositeUser(user.getId())))
                .setHeader("Пользователь")
                .setAutoWidth(true);

        chatGrid.addColumn(chat -> {
                    return getLastMessage(chat).map(it -> UIUtils.formatDate(it.getTime())).orElse("");
                })
                .setHeader("Время")
                .setAutoWidth(true);

        chatGrid.addColumn(chat -> {
                    return getLastMessage(chat).map(ChatMessage::getMessage).orElse("");
                })
                .setHeader("Последнее сообщение")
                .setAutoWidth(true);

        nameFilter.setPlaceholder("Введите имя");
        nameFilter.addValueChangeListener(event -> {
            if (event.getValue() != null) refresh();
        });

        chatGrid.asSingleSelect().addValueChangeListener(event -> {
            Chat value = event.getValue();
            if (value == null) return;
            getUI().ifPresent(ui -> {
                var params = new RouteParameters("userId", String.valueOf(value.getOppositeUser(user.getId()).getId()));
                ui.navigate(ChatView.class, params);
            });
        });

        add(new HorizontalLayout(nameFilter), chatGrid);
    }

    private Optional<ChatMessage> getLastMessage(Chat chat) {
        return chat.getMessages()
                .stream().max(Comparator.comparing(ChatMessage::getTime));
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        user = securityService.getAuthenticatedUser().orElseThrow();
        refresh();
    }

    private void refresh() {
        List<Chat> chats = chatService.findAllChats(user.getId());
        String nameFilterValue = nameFilter.getValue();
        if (nameFilterValue != null && !nameFilterValue.isBlank()) {
            chats = chats.stream()
                    .filter(it -> {
                        return it.getOppositeUser(user.getId())
                                .getName().toLowerCase().contains(nameFilterValue.toLowerCase());
                    })
                    .collect(Collectors.toList());
        }
        chatGrid.setItems(chats);
    }

    public static final String getUserTitle(User user) {
        return user.getName() + " [" + user.getRole().getTitle() + "]";
    }

}
