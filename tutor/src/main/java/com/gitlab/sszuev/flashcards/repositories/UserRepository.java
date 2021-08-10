package com.gitlab.sszuev.flashcards.repositories;

import com.gitlab.sszuev.flashcards.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Created by @ssz on 09.05.2021.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLogin(String name);
}
