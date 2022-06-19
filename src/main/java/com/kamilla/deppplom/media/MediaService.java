package com.kamilla.deppplom.media;

import com.kamilla.deppplom.media.model.Media;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public interface MediaService {

    Media upload(Media media, InputStream input);

    Optional<Media> findById(int id);

    List<Media> findAllByKey(String key);

    void remove(int id);

    byte[] download(int id);

}