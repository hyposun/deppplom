package com.kamilla.deppplom.groups;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class StudentGroupService {

    @Autowired
    private StudentGroupRepository repository;

    public Optional<StudentGroup> findById(int id) {
        return repository.findById(id);
    }

    public StudentGroup update(StudentGroup group) {
        Optional<StudentGroup> existingGroup = repository.findByTitle(group.getTitle());
        if (existingGroup.isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Группа с таким названием уже существует");
        }
        return repository.save(group);
    }

    public List<StudentGroup> findAllByTitleLike(String title) {
        return repository.findAllByTitleLike("%" + title + "%");
    }

    public List<StudentGroup> findAll() {
        return repository.findAll();
    }

    public Page<StudentGroup> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public void delete(int id) {
        repository.deleteById(id);
    }
}
