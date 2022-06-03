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
        int parentId = discipline.getParentId();
        var existing = repository.findByTitleAndParentId(discipline.getTitle(), parentId);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Дисциплина с таким названием уже существует");
        }
        if (parentId != 0) {
            Optional<Discipline> parentDiscipline = findById(parentId);
            if (parentDiscipline.isEmpty()) throw new IllegalArgumentException("Вы пытаетесь привязать тему к несуществущей дисциплине");
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

    public List <Discipline> findAllByParentId(int parentId){
        return repository.findAllByParentId(parentId);
    }

    public Optional <Discipline> findByTitleAndParentId(String title, int parentId){
        return repository.findByTitleAndParentId(title, parentId);
    }


}
