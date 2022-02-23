package com.gitlab.sszuev.flashcards.repositories;

import com.gitlab.sszuev.flashcards.domain.Dictionary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.stream.Stream;

/**
 * A repository to work with {@link Dictionary} entity.
 * <p>
 * Created by @ssz on 02.05.2021.
 */
public interface DictionaryRepository extends JpaRepository<Dictionary, Long> {

    @Query(value = "SELECT d FROM Dictionary d JOIN FETCH d.sourceLang WHERE d.user.id = :userId")
    Stream<Dictionary> streamAllByUserId(long userId);

    @Query(value = "SELECT d FROM Dictionary d JOIN FETCH d.sourceLang")
    Stream<Dictionary> streamAll();

}
