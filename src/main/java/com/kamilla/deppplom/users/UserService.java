package com.kamilla.deppplom.users;

import com.kamilla.deppplom.groups.StudentGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User getUser(int id) {
        return repository.getOne(id);
    }

    public User update(User user) {

        if (user.getId() == 0) {
            Optional<User> existingUser = repository.findByLogin(user.getLogin());
            if (existingUser.isPresent()) {
                throw new IllegalStateException("Пользователем с таким логином уже существует");
            }
        }

        if (user.getRole() == Role.STUDENT) {
            if (user.getGroups().size() <= 0) {
                throw new IllegalStateException("У студента должна быть проставлена группа");
            }
            if (user.getGroups().size() > 1) {
                throw new IllegalStateException("У студента может быть только одна группа");
            }
        }

        processPassword(user);

        return repository.save(user);
    }

    private void processPassword(User user) {

        boolean passwordChanged = repository
                .findById(user.getId())
                .map(User::getPassword)
                .map(currentPasswordHash -> !currentPasswordHash.equals(user.getPassword()))
                .orElse(true);

        if (passwordChanged) {
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
        }
    }

    public List<User> findAllByNameLike(String titleLike) {
        return repository.findAllByNameLike("%" + titleLike + "%");
    }

    public List<User> findAllByRoleAndGroup(int groupId, Role role) {
        StudentGroup group = new StudentGroup();
        group.setId(groupId);
        return repository.findAllByRoleAndGroups(role, group);
    }

    public Page<User> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Optional<User> findById(int id) {
        return repository.findById(id);
    }

    public List<User> findAll(Integer groupId, Role role, String titleLike) {

        var stream = repository.findAll().stream(); // fix me - ну такое

        if (groupId != null) {
            stream = stream.filter(user -> user.getGroups().stream()
                    .anyMatch(group -> group.getId() == groupId));
        }
        if (role != null) {
            stream = stream.filter(user -> user.getRole() == role);
        }
        if (titleLike != null) {
            stream = stream.filter(user -> user.getName().contains(titleLike));
        }

        return stream.collect(Collectors.toList());
    }

    public void delete(int id) {
        repository.deleteById(id);
    }

    public Optional<User> findByLogin(String login) {
        return repository.findByLogin(login);
    }
}
