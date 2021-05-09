package com.gitlab.sszuev.flashcards.domain;

/**
 * Describes the language.
 * <p>
 * Created by @ssz on 29.04.2021.
 */
public interface Language {

    /**
     * Returns the language tag.
     *
     * @return {@code String}, never {@code null}
     */
    String name();
}
