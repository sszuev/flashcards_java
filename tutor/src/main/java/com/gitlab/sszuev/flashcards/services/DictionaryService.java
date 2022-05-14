package com.gitlab.sszuev.flashcards.services;

import com.gitlab.sszuev.flashcards.dto.DictionaryResource;
import org.springframework.core.io.Resource;

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
     * @param xml {@link Resource}, not {@code null}
     * @return {@link DictionaryResource}
     */
    DictionaryResource uploadDictionary(Resource xml);

    /**
     * Deletes the specified dictionary.
     *
     * @param dictionaryId {@code long} id
     */
    void deleteDictionary(long dictionaryId);

    /**
     * Downloads the specified dictionary as a resource to be saved in the client-side.
     *
     * @param dictionaryId {@code long} id
     * @return a {@link Resource} with binary data
     */
    Resource downloadDictionary(long dictionaryId);
}
