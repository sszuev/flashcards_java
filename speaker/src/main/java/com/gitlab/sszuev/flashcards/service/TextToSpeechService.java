package com.gitlab.sszuev.flashcards.service;

import org.springframework.core.io.Resource;

/**
 * A service that provides sound-resource (speech) by the specified text.
 * Created by @ssz on 09.05.2021.
 */
public interface TextToSpeechService {
    /**
     * Returns a resource identifier that corresponds
     * to the given {@code text}, {@code language} tag and {@code options}.
     * The {@code options} array can contain hints to help identify the resource,
     * for example, it may contain a part of speech.
     *
     * @param text     {@code String}, not {@code null}, not empty
     * @param language {@code String}, not {@code null}, e.g. {@code en}
     * @param options  an {@code Array} of {@code String}s
     * @return {@code String} or {@code null} -
     * if no resource found for the specified {@code text}, {@code language} tag and {@code options}
     */
    String getResourceID(String text, String language, String... options);

    /**
     * Returns (Spring) {@code Resource} for the given resource identifier.
     *
     * @param id {@code String} the resource path identifier, not {@code null}
     * @return {@link Resource}, never {@code null}
     */
    Resource getResource(String id);
}
