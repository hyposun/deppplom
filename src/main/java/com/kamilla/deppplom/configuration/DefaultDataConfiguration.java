package com.kamilla.deppplom.configuration;

import com.kamilla.deppplom.discipline.Discipline;
import com.kamilla.deppplom.discipline.DisciplineService;
import com.kamilla.deppplom.groups.StudentGroup;
import com.kamilla.deppplom.groups.StudentGroupRepository;
import com.kamilla.deppplom.users.Role;
import com.kamilla.deppplom.users.User;
import com.kamilla.deppplom.users.UserService;
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

    @Value("${service.default.groups:}")
    private String groups;

    @Value("${service.default.disciplines:}")
    private String disciplines;

    @Autowired
    UserService userService;

    @Autowired
    StudentGroupRepository groupRepository;

    @Autowired
    DisciplineService disciplineService;

    @PostConstruct
    public void setup() {
        handleUser();
        handleGroups();
        handleDisciplines();
    }

    private void handleDisciplines() {
        if (groups == null || isBlank(disciplines)) return;
        for (String disciplineTitle : disciplines.split(",")) {
            Optional<Discipline> existing = disciplineService.findByTitle(disciplineTitle);
            if (existing.isPresent()) continue;
            Discipline discipline = new Discipline();
            discipline.setTitle(disciplineTitle);
            disciplineService.update(discipline);
        }
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
        var admin = userService.findByLogin(adminLogin);
        if (admin.isPresent()) return;

        var user = new User();
        user.setId(0);
        user.setName("Администратор");
        user.setLogin(adminLogin);
        user.setPassword(adminPassword);
        user.setRole(Role.ADMIN);
        user = userService.update(user);
        log.info("Зарегистрирована учетная запись администратора:" + user);
    }

}
