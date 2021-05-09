package com.gitlab.sszuev.flashcards.service;

import org.springframework.core.io.Resource;

/**
 * Created by @ssz on 09.05.2021.
 */
public interface TextToSpeechService {
    /**
     * Returns a resource path identifier that corresponds the given {@code text} and {@code language} tag.
     *
     * @param text     {@code String}, not {@code null}, not empty
     * @param language {@code String}, not {@code null}, e.g. {@code en}
     * @return {@code String} or {@code null} (if no resource found for the specified text + lang)
     */
    String getResourceID(String text, String language);

    /**
     * Returns {@code Resource} for the given resource identifier (name).
     *
     * @param id {@code String} the resource path identifier, not {@code null}
     * @return {@link Resource}, never {@code null}
     */
    Resource getResource(String id);
}
