package com.kamilla.deppplom.media.impl.db;

import com.kamilla.deppplom.media.MediaService;
import com.kamilla.deppplom.media.model.Media;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DBMediaService implements MediaService {

    @Autowired
    private DBMediaRepository repository;

    @Override
    public Optional<Media> findById(int id) {
        return repository.findById(id).map(this::fromEntity);
    }

    @Override
    @SneakyThrows
    public Media upload(Media media, InputStream input) {
        DBMediaEntity entity = toEntity(media, input);
        entity = repository.save(entity);
        return fromEntity(entity);
    }

    @Override
    public List<Media> findAllByKey(String key) {
        return repository.findAllByKey(key).stream()
                .map(this::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void remove(int id) {
        repository.deleteById(id);
    }

    @Override
    public byte[] download(int id) {
        return repository
                .findById(id)
                .map(DBMediaEntity::getContent)
                .orElseThrow();
    }

    @SneakyThrows
    private DBMediaEntity toEntity(Media media, InputStream inputStream) {
        DBMediaEntity entity = new DBMediaEntity();
        entity.setId(media.getId());
        entity.setKey(media.getKey());
        entity.setName(media.getName());
        entity.setContent(inputStream.readAllBytes());
        return entity;
    }

    private Media fromEntity(DBMediaEntity entity) {
        return new Media(
            entity.getId(),
            entity.getKey(),
            entity.getName()
        );
    }

}
