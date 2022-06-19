package com.kamilla.deppplom.media.impl.db;

import lombok.Data;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@Data
@Entity
@Table
public class DBMediaEntity {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private int id;

    @Column
    private String key;

    @Column(nullable = false)
    private String name;

    @Lob
    @Basic(fetch = LAZY)
    private byte[] content;

}