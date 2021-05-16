package com.gitlab.sszuev.flashcards.dao;

import com.gitlab.sszuev.flashcards.domain.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.stream.Stream;

/**
 * A repository to work with {@link Card} entities.
 * <p>
 * Created by @ssz on 15.05.2021.
 */
public interface CardRepository extends JpaRepository<Card, Long> {

    Stream<Card> streamAllByIdIn(Collection<Long> ids);
}
