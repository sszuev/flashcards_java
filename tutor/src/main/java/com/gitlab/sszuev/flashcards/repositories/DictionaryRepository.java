package com.gitlab.sszuev.flashcards.repositories;

import com.gitlab.sszuev.flashcards.domain.Dictionary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.stream.Stream;

/**
 * A repository to work with {@link Dictionary} entity.
 * <p>
 * Created by @ssz on 02.05.2021.
 */
public interface DictionaryRepository extends JpaRepository<Dictionary, Long> {

    Stream<Dictionary> streamAllByUserId(long userId);

}
