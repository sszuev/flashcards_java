package com.gitlab.sszuev.flashcards.services;

import org.springframework.core.io.Resource;

/**
 * Created by @ssz on 08.05.2021.
 */
public interface SoundService {
    /**
     * Returns a resource identifier (name) that corresponds the given {@code word}.
     *
     * @param word {@code String} the word, not {@code null}
     * @param lang {@code String} the language, not {@code null}
     * @return {@code String} (name of resource) or {@code null} (if no resource found by the specified word)
     */
    String getResourceName(String word, String lang);

    /**
     * Returns {@code Resource} for the given resource identifier (name).
     *
     * @param name {@code String} the resource name, not {@code null}
     * @return {@link Resource}, never {@code null}
     */
    Resource getResource(String name);
}
