package com.gitlab.sszuev.flashcards.domain;

/**
 * Created by @ssz on 01.05.2021.
 *
 * @see <a href='https://en.wikipedia.org/wiki/Part_of_speech'>Part_of_speech</a>
 */
public interface PartOfSpeech {
    /**
     * Returns the part-of-speech name.
     *
     * @return {@code String}, never {@code null}
     */
    String name();

    /**
     * Gets {@link PartOfSpeech} that corresponds the given {@code name}.
     *
     * @param name {@code String}, can be {@code null}
     * @return {@link PartOfSpeech} or {@code null}
     */
    static PartOfSpeech fromString(String name) {
        if (name == null) return null;
        try {
            return StandardPartOfSpeech.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException ignore) {
            return () -> name;
        }
    }

    /**
     * Gets the name of the specified {@link PartOfSpeech}
     *
     * @param partOfSpeech {@link PartOfSpeech}, can be {@code null}
     * @return {@code String} or {@code null}
     */
    static String toString(PartOfSpeech partOfSpeech) {
        return partOfSpeech == null ? null : partOfSpeech.name();
    }
}
