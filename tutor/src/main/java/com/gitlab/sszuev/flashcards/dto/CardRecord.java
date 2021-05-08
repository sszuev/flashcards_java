package com.gitlab.sszuev.flashcards.dto;

/**
 * Created by @ssz on 02.05.2021.
 */
public final class CardRecord {
    private final String word;
    private final String translations;
    private final String sound;

    public CardRecord(String word, String translations, String sound) {
        this.word = word;
        this.translations = translations;
        this.sound = sound;
    }

    public String getSound() {
        return sound;
    }

    public String getWord() {
        return word;
    }

    public String getTranslations() {
        return translations;
    }
}
