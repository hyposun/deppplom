package com.kamilla.deppplom.tests.controller;

import com.kamilla.deppplom.tests.TestService;
import com.kamilla.deppplom.tests.model.CreateRandomizedTestVariantRequest;
import com.kamilla.deppplom.tests.model.CreateTestRequest;
import com.kamilla.deppplom.tests.model.ManuallyCreateTestVersionRequest;
import com.kamilla.deppplom.tests.model.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/api/v0/tests")
public class TestController {

    @Autowired
    private TestService service;

    @GetMapping("/{testId}")
    public Test get(
            @PathVariable int testId
    ) {
        return service.findById(testId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public List<Test> findAll(@RequestParam @Positive int disciplineId) {
        return service.findAll(disciplineId);
    }

    @PostMapping
    public Test createTest(@RequestBody @Valid CreateTestRequest request) {
        return service.createTest(request);
    }

    @PostMapping("/versions/manual")
    public Test manuallyCreateTestVersion(@RequestBody @Valid ManuallyCreateTestVersionRequest request) {
        return service.manuallyCreateVersion(request);
    }

    @PostMapping("/versions/random")
    public Test randomizedCreateTestVersion(@RequestBody @Valid CreateRandomizedTestVariantRequest request) {
        return service.createRandomizedVariants(request);
    }


}
