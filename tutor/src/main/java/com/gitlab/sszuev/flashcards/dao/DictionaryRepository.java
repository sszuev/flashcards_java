package com.gitlab.sszuev.flashcards.dao;

import com.gitlab.sszuev.flashcards.domain.Dictionary;

import java.util.Optional;
import java.util.stream.Stream;

/**
 * A repository to work with {@link Dictionary} entity.
 * <p>
 * Created by @ssz on 02.05.2021.
 */
public interface DictionaryRepository {

    Optional<Dictionary> findByUserIdAndName(long userId, String name);

    Stream<Dictionary> streamAllByUserId(long userId);

}
