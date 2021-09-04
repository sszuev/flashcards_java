package com.gitlab.sszuev.flashcards.services.impl;

import com.gitlab.sszuev.flashcards.domain.User;
import com.gitlab.sszuev.flashcards.dto.AccountRequest;
import com.gitlab.sszuev.flashcards.repositories.UserRepository;
import com.gitlab.sszuev.flashcards.services.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * Created by @ssz on 04.09.2021.
 */
@Service
public class AccountServiceImpl implements AccountService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountService.class);
    private final UserRepository repository;
    private final PasswordEncoder encoder;

    public AccountServiceImpl(UserRepository repository, PasswordEncoder encoder) {
        this.repository = Objects.requireNonNull(repository);
        this.encoder = Objects.requireNonNull(encoder);
    }

    @Transactional
    @Override
    public boolean save(AccountRequest user) {
        if (repository.findByLogin(user.getLogin()).isPresent()) {
            LOGGER.info("User '{}' already exists", user.getLogin());
            return false;
        }
        LOGGER.info("Create '{}'", user.getLogin());
        repository.save(createUser(user));
        return true;
    }

    private User createUser(AccountRequest user) {
        User res = new User();
        res.setLogin(user.getLogin());
        res.setPassword(encoder.encode(user.getPassword()));
        res.setRole(RoleMapper.Role.USER.code());
        return res;
    }

}
