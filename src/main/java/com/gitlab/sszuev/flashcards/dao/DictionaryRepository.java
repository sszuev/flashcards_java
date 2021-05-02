package com.gitlab.sszuev.flashcards.dao;

import com.gitlab.sszuev.flashcards.domain.Dictionary;

import java.util.Optional;

/**
 * A repository to work with {@link Dictionary} entity.
 *
 * Created by @ssz on 02.05.2021.
 */
public interface DictionaryRepository {

    Optional<Dictionary> findByName(String name);

}
