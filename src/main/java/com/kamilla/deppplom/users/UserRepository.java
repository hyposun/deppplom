package com.kamilla.deppplom.users;

import com.kamilla.deppplom.groups.StudentGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    List<User> findAllByNameLike(String name);

    Optional<User> findByLogin(String login);

    List<User> findAllByRoleAndGroups(Role role, StudentGroup group);

}
