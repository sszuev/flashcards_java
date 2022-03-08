package com.gitlab.sszuev.flashcards.services;

import com.gitlab.sszuev.flashcards.dto.CardResource;
import com.gitlab.sszuev.flashcards.dto.CardUpdateResource;
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
     * Returns all cards from the specified dictionary,
     * the result is sorted in natural order (by {@link CardResource#word()}).
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
     * @param data a {@code List} of {@link CardUpdateResource}s
     */
    void update(List<CardUpdateResource> data);

    /**
     * Saves the given {@link CardResource} into db.
     *
     * @param resource {@link CardResource}
     * @return {@code long} - if of card
     */
    long save(CardResource resource);

    /**
     * Deletes the specified card.
     *
     * @param cardId {@code long} card id
     */
    void deleteCard(long cardId);
}
