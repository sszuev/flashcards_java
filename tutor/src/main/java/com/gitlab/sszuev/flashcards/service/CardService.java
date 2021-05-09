package com.gitlab.sszuev.flashcards.service;

import com.gitlab.sszuev.flashcards.dto.CardRecord;

import java.util.List;

/**
 * Created by @ssz on 02.05.2021.
 */
public interface CardService {
    /**
     * Gets card by dictionary name and index within that dictionary.
     *
     * @param dictionary {@code String}, not {@code null}
     * @param index      {@code Integer}
     * @return {@link CardRecord} (can be {@code null})
     */
    CardRecord getCard(String dictionary, Integer index);

    /**
     * List all dictionary names.
     *
     * @return a {@code List} of names
     */
    List<String> getDictionaryNames();
}
