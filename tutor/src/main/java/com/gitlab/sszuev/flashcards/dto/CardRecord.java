package com.gitlab.sszuev.flashcards.dto;

import java.util.Map;

/**
 * Created by @ssz on 02.05.2021.
 */
@SuppressWarnings("unused")
public final class CardRecord extends CardRequest {
    private final String word;
    private final String translations;
    private final String sound;
    private final int answered;

    public CardRecord(long id, String word, String translations, String sound, int answered, Map<Stage, Boolean> details) {
        super(id, details);
        this.word = word;
        this.translations = translations;
        this.sound = sound;
        this.answered = answered;
    }

    public int getAnswered() {
        return answered;
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
