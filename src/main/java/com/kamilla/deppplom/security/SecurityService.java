package com.kamilla.deppplom.security;

import com.kamilla.deppplom.users.User;
import com.kamilla.deppplom.users.UserRepository;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SecurityService {

    private static final String LOGOUT_SUCCESS_URL = "/";

    @Autowired
    private UserRepository repository;

    public Optional<User> getAuthenticatedUser() {
        SecurityContext context = SecurityContextHolder.getContext();
        UserDetails principal = (UserDetails) context.getAuthentication().getPrincipal();
        return repository.findByLogin(principal.getUsername());
    }

    public void logout() {
        UI.getCurrent().getPage().setLocation(LOGOUT_SUCCESS_URL);
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(
                VaadinServletRequest.getCurrent().getHttpServletRequest(), null,
                null);
    }
}
