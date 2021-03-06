package com.kamilla.deppplom.security;

import com.kamilla.deppplom.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class CustomUserDatailsService implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByLogin(username)
                .map(user -> User.builder()
                    .username(user.getLogin())
                    .password(user.getPassword())
                    .roles(user.getRole().name())
                    .disabled(user.isDisabled())
                    .build()
                )
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }
}
