package com.kamilla.deppplom.tests.controller;

import com.kamilla.deppplom.tests.repository.model.TestEntity;
import com.kamilla.deppplom.tests.repository.TestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
public class TestController {

    @Autowired
    TestRepository repository;

    @GetMapping("/by_discipline_id")
    public List<TestEntity> findByDisciplineId(@RequestParam @Valid int disciplineId) {
        List <TestEntity> tests = repository.findByDisciplineId(disciplineId);
        if (tests.isEmpty())  throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"По данной дисциплине не был найден список тестов");
        return tests;
    }

    @GetMapping("/by_title")
    public TestEntity findByTitleLike(@RequestParam @Valid String title) {
        Optional<TestEntity> test = repository.findByTitleLike(title);
        if (test.isEmpty()) { throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Тест по данной теме не был найден"); }

        else return test.get();
    }

//    @PostMapping
//    public Test create(){
//
//    }
}
