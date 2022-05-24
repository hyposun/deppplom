package com.kamilla.deppplom.configuration;

import com.kamilla.deppplom.users.Role;
import com.kamilla.deppplom.users.User;
import com.kamilla.deppplom.users.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotBlank;

@Configuration
public class DefaultDataConfiguration {

    private static final Logger log = LoggerFactory.getLogger(DefaultDataConfiguration.class);

    @Value("${service.default.adminLogin}")
    @NotBlank
    private String adminLogin;

    @Value("${service.default.adminPassword}")
    @NotBlank
    private String adminPassword;

    @Autowired
    UserRepository repository;

    @PostConstruct
    public void setup() {
        var admin = repository.findByLogin(adminLogin);
        if (admin.isPresent()) return;

        var user = new User();
        user.setId(0);
        user.setName("Администратор");
        user.setLogin(adminLogin);
        user.setPassword(adminPassword);
        user.setRole(Role.ADMIN);
        user = repository.save(user);
        log.info("Зарегистрирована учетная запись администратора:" + user);
    }

}
