package com.gitlab.sszuev.flashcards.dto;

/**
 * Created by @ssz on 02.05.2021.
 */
public final class CardRecord {
    private final String word;
    private final String translations;

    public CardRecord(String word, String translations) {
        this.word = word;
        this.translations = translations;
    }

    public String getWord() {
        return word;
    }

    public String getTranslations() {
        return translations;
    }
}
