package com.gitlab.sszuev.flashcards.service;

import com.gitlab.sszuev.flashcards.AudioResourceProvider;

/**
 * A gateway that provides audio resource with speech by the specified text.
 * <p>
 * Created by @ssz on 09.05.2021.
 */
public interface TextToSpeechService extends AudioResourceProvider {
    /**
     * Returns a resource identifier that corresponds
     * to the given {@code text}, {@code language} tag and {@code options}.
     * The {@code options} array may contain hints to help identify the resource,
     * for example, it may contain a part of speech.
     *
     * @param text     {@code String}, not {@code null}, not empty
     * @param language {@code String}, not {@code null}, e.g. {@code en}
     * @param options  an {@code Array} of {@code String}s, possible empty
     * @return {@code String} or {@code null} in case no resource found
     */
    String getResourceID(String text, String language, String... options);
}
