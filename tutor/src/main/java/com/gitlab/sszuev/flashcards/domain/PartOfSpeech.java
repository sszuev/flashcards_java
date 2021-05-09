package com.gitlab.sszuev.flashcards.domain;

/**
 * Created by @ssz on 01.05.2021.
 * @see <a href='https://en.wikipedia.org/wiki/Part_of_speech'>Part_of_speech</a>
 */
public interface PartOfSpeech {

    /**
     * Returns the part-of-speech name.
     *
     * @return {@code String}, never {@code null}
     */
    String name();
}
