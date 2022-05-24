package com.kamilla.deppplom.users.controller;

import com.kamilla.deppplom.groups.StudentGroup;
import com.kamilla.deppplom.users.Role;
import com.kamilla.deppplom.users.User;
import com.kamilla.deppplom.users.UserRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Tag(name = "User API", description = "Работа с пользователями")
@RestController
@RequestMapping("/api/v0/users")
public class UserController {

    @Autowired
    UserRepository repository;

    @PostMapping("/update")
    public User updateUser(@RequestBody @Valid User user){

        if (user.getId() == 0) {
            Optional<User> existingUser = repository.findByLogin(user.getLogin());
            if(existingUser.isPresent()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Пользователем с таким логином уже существует");
            }
        }

        if (user.getRole() == Role.STUDENT) {
            if (user.getGroups().size() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "У студента должна быть проставлена группа");
            }
            if (user.getGroups().size() > 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "У студента может быть только одна группа");
            }
        }
        return repository.save(user);
    }

    @GetMapping("/findAllByNameLike")
    public List<User> findAllByNameLike(String name) {
        return repository.findAllByNameLike("%" + name + "%");
    }

    @GetMapping("/findAllByGroup")
    public List<User> findAllByGroups(int groupId, Role role) {
        StudentGroup group = new StudentGroup();
        group.setId(groupId);
        return repository.findAllByRoleAndGroups(role, group);
    }

    @GetMapping
    public Page<User> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "25") int size) {
        var pageable = PageRequest.of(page, size);
        return repository.findAll(pageable);
    }

}
