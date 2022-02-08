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
     * Returns all cards by dictionary.
     *
     * @param dictionaryId {@code long} - id of dictionary
     * @return a {@code List} of {@link CardResource}s
     */
    List<CardResource> getAllCards(long dictionaryId);

    /**
     * Returns a deck ({@code List}) of cards to proceed by peeking them randomly from the specified dictionary.
     * The method is not-idempotent: each its call should return a new deck different from previous one.
     *
     * @param dictionaryId {@code long} - id of dictionary
     * @return a {@code List} of {@link CardResource}s - every time a new one
     */
    List<CardResource> getNextCardDeck(long dictionaryId);

    /**
     * Returns a deck ({@code List}) of cards to proceed by peeking them randomly from the specified dictionary.
     * The method is not-idempotent: each its call should return a new deck different from previous one.
     *
     * @param dictionaryId {@code long} - id of dictionary
     * @param length       {@code int} - the desired length of returned {@code List},
     *                     note that actual length may differ from the specified value
     * @param unknown      {@code boolean} - if {@code true} returns only unknown cards, otherwise cards with any status
     * @return a {@code List} of {@link CardResource}s - every time a new one
     */
    List<CardResource> getNextCardDeck(long dictionaryId, int length, boolean unknown);

    /**
     * Updates cards by applying the specifies data.
     *
     * @param data a {@code List} of {@link CardRequest}s
     */
    void update(List<CardRequest> data);
}
