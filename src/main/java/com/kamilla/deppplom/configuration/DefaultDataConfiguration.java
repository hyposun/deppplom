package com.kamilla.deppplom.configuration;

import com.kamilla.deppplom.groups.StudentGroup;
import com.kamilla.deppplom.groups.StudentGroupRepository;
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
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Configuration
public class DefaultDataConfiguration {

    private static final Logger log = LoggerFactory.getLogger(DefaultDataConfiguration.class);

    @Value("${service.default.adminLogin}")
    @NotBlank
    private String adminLogin;

    @Value("${service.default.adminPassword}")
    @NotBlank
    private String adminPassword;

    @Value("${service.default.groups}")
    private String groups;

    @Autowired
    UserRepository userRepository;

    @Autowired
    StudentGroupRepository groupRepository;

    @PostConstruct
    public void setup() {
        handleUser();
        handleGroups();
    }

    private void handleGroups() {
        if (groups == null || isBlank(groups)) return;
        for (String groupTitle : groups.split(",")) {
            Optional<StudentGroup> existingGroup = groupRepository.findByTitle(groupTitle);
            if (existingGroup.isPresent()) continue;
            StudentGroup group = new StudentGroup();
            group.setTitle(groupTitle);
            groupRepository.save(group);
        }
    }

    private void handleUser() {
        var admin = userRepository.findByLogin(adminLogin);
        if (admin.isPresent()) return;

        var user = new User();
        user.setId(0);
        user.setName("Администратор");
        user.setLogin(adminLogin);
        user.setPassword(adminPassword);
        user.setRole(Role.ADMIN);
        user = userRepository.save(user);
        log.info("Зарегистрирована учетная запись администратора:" + user);
    }

}
