package com.gitlab.sszuev.flashcards.service;

import com.gitlab.sszuev.flashcards.dto.CardRequest;
import com.gitlab.sszuev.flashcards.dto.CardResource;

import java.util.List;

/**
 * Created by @ssz on 02.05.2021.
 */
public interface CardService {
    /**
     * Gets card by dictionary name and index within that dictionary.
     * TODO: unused now: either remove method or this warning
     *
     * @param dictionary {@code String}, not {@code null}
     * @param index      {@code Integer}
     * @return {@link CardResource} (can be {@code null})
     */
    CardResource getCard(String dictionary, Integer index);

    /**
     * List all dictionary names.
     *
     * @return a {@code List} of names
     */
    List<String> getDictionaryNames();

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
