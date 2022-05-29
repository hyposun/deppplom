package com.kamilla.deppplom.ui;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginI18n.Form;
import com.vaadin.flow.component.login.LoginI18n.Header;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("login")
@PageTitle("Авторизация | Deppplom")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm login = new LoginForm();

    public LoginView() {
        addClassName("login-view");

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        login.setForgotPasswordButtonVisible(false);
        login.setAction("login");

        LoginI18n i18n = LoginI18n.createDefault();

        Header header = new Header();
        header.setTitle("Deppplom");
        header.setDescription("");
        i18n.setHeader(header);

        Form form = new Form();
        form.setTitle("");
        form.setSubmit("Войти");
        form.setUsername("Логин");
        form.setPassword("Пароль");
        i18n.setForm(form);

        LoginI18n.ErrorMessage message = new LoginI18n.ErrorMessage();
        message.setTitle("Неверное имя пользователя или пароль");
        message.setMessage("Пожалуйста, проверьте правильность имени пользователя и пароля и повторите попытку.");
        i18n.setErrorMessage(message);

        login.setI18n(i18n);

        add(new H1("Deppplom"), login);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (beforeEnterEvent.getLocation()
                .getQueryParameters()
                .getParameters()
                .containsKey("error")) {
            login.setError(true);
        }
    }
}
