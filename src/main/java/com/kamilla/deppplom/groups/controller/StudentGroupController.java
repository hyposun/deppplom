package com.kamilla.deppplom.groups.controller;


import com.kamilla.deppplom.groups.StudentGroup;
import com.kamilla.deppplom.groups.StudentGroupRepository;
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

@Tag(name = "Student group API", description = "Работа с группами учеников")
@RestController
@RequestMapping("/api/v0/student_groups/")
public class StudentGroupController {

    @Autowired
    private StudentGroupRepository repository;

    @PostMapping
    public StudentGroup update(@RequestBody @Valid StudentGroup group) {
        Optional<StudentGroup> existingGroup = repository.findByTitle(group.getTitle());
        if (existingGroup.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Группа с таким названием уже существует");
        }
        return repository.save(group);
    }

    @GetMapping("/findAllByTitleLike")
    public List<StudentGroup> findAllByTitleLike(String title) {
        return repository.findAllByTitleLike("%" + title + "%");
    }

    @GetMapping
    public Page<StudentGroup> findAll(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "25") int size) {
        var pageable = PageRequest.of(page, size);
        return repository.findAll(pageable);
    }

}
