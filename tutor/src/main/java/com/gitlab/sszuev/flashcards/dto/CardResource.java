package com.gitlab.sszuev.flashcards.dto;

import java.util.List;
import java.util.Map;

/**
 * Created by @ssz on 02.05.2021.
 */
@SuppressWarnings("unused")
public record CardResource(long id,
                           String word,
                           String transcription,
                           String partOfSpeech,
                           List<List<String>> translations,
                           List<String> examples,
                           String sound,
                           int answered,
                           Map<Stage, Boolean> details) {

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

    public List<List<String>> getTranslations() {
        return translations;
    }

    public Map<Stage, Boolean> getDetails() {
        return details;
    }

    @Override
    public String transcription() {
        return transcription;
    }

    @Override
    public List<String> examples() {
        return examples;
    }

    @Override
    public String partOfSpeech() {
        return partOfSpeech;
    }
}
