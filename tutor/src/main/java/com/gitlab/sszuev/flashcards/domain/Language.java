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

    /**
     * Gets {@link Language} that corresponds the given {@code lang}.
     *
     * @param lang {@code String}, can be {@code null}
     * @return {@link Language} or {@code null}
     */
    static Language fromString(String lang) {
        if (lang == null) return null;
        try {
            return StandardLanguage.valueOf(lang.toUpperCase());
        } catch (IllegalArgumentException ignore) {
            return () -> lang;
        }
    }

    /**
     * Gets lang-tag from the specified {@link Language}
     *
     * @param lang {@link Language}, can be {@code null}
     * @return {@code String} or {@code null}
     */
    static String toString(Language lang) {
        return lang == null ? null : lang.name();
    }
}
