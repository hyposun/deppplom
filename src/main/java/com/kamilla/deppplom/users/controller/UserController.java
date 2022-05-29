package com.kamilla.deppplom.users.controller;

import com.kamilla.deppplom.users.Role;
import com.kamilla.deppplom.users.User;
import com.kamilla.deppplom.users.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "User API", description = "Работа с пользователями")
@RestController
@RequestMapping("/api/v0/users")
public class UserController {

    @Autowired
    private UserService service;

    @PostMapping("/update")
    public User updateUser(@RequestBody @Valid User user){
        return service.update(user);
    }

    @GetMapping("/findAllByNameLike")
    public List<User> findAllByNameLike(String name) {
        return service.findAllByNameLike(name);
    }

    @GetMapping("/findAllByGroup")
    public List<User> findAllByGroups(int groupId, Role role) {
        return service.findAllByRoleAndGroup(groupId, role);
    }

    @GetMapping
    public Page<User> findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "25") int size) {
        var pageable = PageRequest.of(page, size);
        return service.findAll(pageable);
    }


}
