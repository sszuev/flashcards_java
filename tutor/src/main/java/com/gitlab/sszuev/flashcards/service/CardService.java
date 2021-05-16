package com.gitlab.sszuev.flashcards.service;

import com.gitlab.sszuev.flashcards.dto.CardRequest;
import com.gitlab.sszuev.flashcards.dto.CardResource;
import com.gitlab.sszuev.flashcards.dto.DictionaryResource;

import java.util.List;

/**
 * Created by @ssz on 02.05.2021.
 */
public interface CardService {

    /**
     * Returns dictionaries common info.
     *
     * @return a {@code List} of {@link DictionaryResource}s
     */
    List<DictionaryResource> getDictionaries();

    /**
     * Returns a {@code List} of cards to proceed.
     *
     * @param dictionary {@code String}, not {@code null}
     * @return a {@code List} of {@link CardResource}s
     */
    List<CardResource> getCardDeck(String dictionary);

    /**
     * Updates cards by applying the specifies data.
     *
     * @param data a {@code List} of {@link CardRequest}s
     */
    void update(List<CardRequest> data);
}
