package com.kamilla.deppplom.discipline;

import com.kamilla.deppplom.discipline.repository.DisciplineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DisciplineService {

    @Autowired
    private DisciplineRepository repository;

    public Discipline update(Discipline discipline) {
        var existing = repository.findByTitle(discipline.getTitle());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Дисциплина с таким названием уже существует");
        }
        return repository.save(discipline);
    }

    public Optional<Discipline> findById(int id) {
        return repository.findById(id);
    }

    public Optional<Discipline> findByTitle(String title) {
        return repository.findByTitle(title);
    }

    public List<Discipline> findAllByTitleLike(String titleLike) {
        return repository.findAllByTitleLike( "%" + titleLike + "%");
    }

    public List<Discipline> findAll() {
        return repository.findAll();
    }


}
