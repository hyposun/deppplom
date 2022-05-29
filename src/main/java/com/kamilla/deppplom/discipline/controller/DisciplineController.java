package com.kamilla.deppplom.discipline.controller;

import com.kamilla.deppplom.discipline.Discipline;
import com.kamilla.deppplom.discipline.DisciplineService;
import com.kamilla.deppplom.discipline.repository.DisciplineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

import static org.apache.commons.lang3.StringUtils.isBlank;

@RestController
@RequestMapping("/api/v0/disciplines")
class DisciplineController {

    @Autowired
    private DisciplineRepository repository;

    @Autowired
    private DisciplineService service;

    @PostMapping
    public Discipline create(@RequestBody @Valid Discipline discipline){
        return service.update(discipline);
    }

    @GetMapping
    public Page<Discipline> findAll(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "25") int size,
                                    @RequestParam(required = false) String titleLike
    ){
        var pageable = PageRequest.of(page, size);
        if (titleLike != null && !isBlank(titleLike)) {
            return repository.findAllByTitleLike("%" + titleLike + "%", pageable);
        } else {
            return repository.findAll(pageable);
        }
    }

}

