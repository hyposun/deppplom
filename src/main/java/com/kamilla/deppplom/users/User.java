package com.kamilla.deppplom.users;

import com.kamilla.deppplom.groups.StudentGroup;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@Table
@Data
public class User {

    @Id
    @GeneratedValue
    private int id;

    @Column(nullable = false)
    @NotBlank
    private String name;

    @Column(unique = true)
    @Size(min = 2,message = "Логин должен состоять не менее из двух символов")
    private String login;

    @Column(nullable = false)
    @Size(min = 2, message = "Пароль должен состоять не менее из двух символов")
    private String password;

    @Column(nullable = false)
    private Role role;

    @OneToMany
    private Set<StudentGroup> groups;

}

