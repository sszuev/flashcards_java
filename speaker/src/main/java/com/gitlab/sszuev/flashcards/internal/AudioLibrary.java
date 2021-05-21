package com.gitlab.sszuev.flashcards.internal;

import com.gitlab.sszuev.flashcards.AudioResourceProvider;

/**
 * A common interface that provides access to sound resource by the text and text details.
 * <p>
 * Created by @ssz on 19.05.2021.
 */
public interface AudioLibrary extends AudioResourceProvider {
    /**
     * Returns a resource identifier that corresponds to the given {@code text} and {@code options}.
     * The {@code options} array may contain hints to help identify the resource,
     * for example, it may contain a part of speech.
     *
     * @param text    {@code String}, not {@code null}, not empty
     * @param options an {@code Array} of {@code String}s, can be empty
     * @return {@code String} or {@code null} (if no resource found)
     */
    String getResourceID(String text, String... options);
}
