package com.gitlab.sszuev.flashcards.services;

import com.gitlab.sszuev.flashcards.dto.DictionaryResource;

import java.util.List;

public interface DictionaryService {

    /**
     * Returns dictionaries common info.
     *
     * @return a {@code List} of {@link DictionaryResource}s
     */
    List<DictionaryResource> getDictionaries();

    /**
     * Uploads dictionary given as a string.
     *
     * @param xml {@code String}, not {@code null}
     * @return {@link DictionaryResource}
     */
    DictionaryResource uploadDictionary(String xml);
}
