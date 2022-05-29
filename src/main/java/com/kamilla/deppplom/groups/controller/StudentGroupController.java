package com.kamilla.deppplom.groups.controller;


import com.kamilla.deppplom.groups.StudentGroup;
import com.kamilla.deppplom.groups.StudentGroupService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "Student group API", description = "Работа с группами учеников")
@RestController
@RequestMapping("/api/v0/student_groups/")
public class StudentGroupController {

    @Autowired
    private StudentGroupService service;

    @PostMapping
    public StudentGroup update(@RequestBody @Valid StudentGroup group) {
        return service.update(group);
    }

    @GetMapping("/findAllByTitleLike")
    public List<StudentGroup> findAllByTitleLike(String title) {
        return service.findAllByTitleLike(title);
    }

    @GetMapping
    public Page<StudentGroup> findAll(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "25") int size) {
        var pageable = PageRequest.of(page, size);
        return service.findAll(pageable);
    }

}


