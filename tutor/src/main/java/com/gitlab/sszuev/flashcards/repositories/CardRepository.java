package com.gitlab.sszuev.flashcards.repositories;

import com.gitlab.sszuev.flashcards.domain.Card;
import com.gitlab.sszuev.flashcards.domain.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.stream.Stream;

/**
 * A repository to work with {@link Card} entities.
 * <p>
 * Created by @ssz on 15.05.2021.
 */
public interface CardRepository extends JpaRepository<Card, Long> {

    Stream<Card> streamByIdIn(Collection<Long> ids);

    @Query(value = "SELECT c FROM Card c JOIN FETCH c.translations LEFT JOIN FETCH c.examples " +
            "WHERE c.dictionary.id = :dictionaryId AND c.status IN :statuses")
    Stream<Card> streamByDictionaryIdAndStatusIn(@Param("dictionaryId") long dictionaryId,
                                                 @Param("statuses") Collection<Status> statuses);

}
