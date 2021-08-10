package com.gitlab.sszuev.flashcards.services.impl;


import com.gitlab.sszuev.flashcards.domain.User;
import com.gitlab.sszuev.flashcards.repositories.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by @ssz on 01.08.2021.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository repository;
    private final Function<Integer, Stream<String>> roleMapper;

    public UserDetailsServiceImpl(UserRepository repository, RoleMapper roleMapper) {
        this.repository = Objects.requireNonNull(repository);
        this.roleMapper = Objects.requireNonNull(roleMapper);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User res = repository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("No user with login " + username));
        Set<GrantedAuthority> authorities = roleMapper.apply(res.getRole())
                .map(x -> new SimpleGrantedAuthority("ROLE_" + x)).collect(Collectors.toSet());
        return new org.springframework.security.core.userdetails.User(res.getLogin(), res.getPassword(), authorities);
    }
}

