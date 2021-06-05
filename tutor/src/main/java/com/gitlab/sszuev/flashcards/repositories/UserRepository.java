package com.gitlab.sszuev.flashcards.repositories;

import com.gitlab.sszuev.flashcards.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by @ssz on 09.05.2021.
 */
public interface UserRepository extends JpaRepository<User, Long> {
}
