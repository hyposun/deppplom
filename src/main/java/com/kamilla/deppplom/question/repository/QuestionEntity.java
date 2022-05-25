package com.kamilla.deppplom.question.repository;

import lombok.Data;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Table
@Entity
@Data
class QuestionEntity {

    @Id
    @GeneratedValue
    private int id;

    private int topicId;

    private String title;

    private String type;

    private String body;

}