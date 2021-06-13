package com.gitlab.sszuev.flashcards.services;

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
     * @param dictionaryId {@code long}
     * @return a {@code List} of {@link CardResource}s
     */
    List<CardResource> getNextCardDeck(long dictionaryId);

    /**
     * Returns a {@code List} of (randomized) cards to proceed.
     * It is non-idempotent method.
     *
     * @param length  {@code int} - the desired length of returned {@code List},
     *                note that actual length may differ from the specified value
     * @param unknown {@code boolean} - if {@code true} returns only unknown cards
     * @return a {@code List} of {@link CardResource}s - every time new
     */
    List<CardResource> getNextCardDeck(long dictionaryId, int length, boolean unknown);

    /**
     * Updates cards by applying the specifies data.
     *
     * @param data a {@code List} of {@link CardRequest}s
     */
    void update(List<CardRequest> data);
}
