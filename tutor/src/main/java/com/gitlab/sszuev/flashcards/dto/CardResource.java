package com.gitlab.sszuev.flashcards.dto;

import java.util.Map;

/**
 * Created by @ssz on 02.05.2021.
 */
@SuppressWarnings("unused")
public final class CardResource {
    private final String word;
    private final String translations;
    private final String sound;
    private final int answered;
    private final long id;
    private final Map<Stage, Boolean> details;

    public CardResource(long id, String word, String translations, String sound, int answered, Map<Stage, Boolean> details) {
        this.id = id;
        this.details = details;
        this.word = word;
        this.translations = translations;
        this.sound = sound;
        this.answered = answered;
    }

    public long getId() {
        return id;
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

    public Map<Stage, Boolean> getDetails() {
        return details;
    }
}
