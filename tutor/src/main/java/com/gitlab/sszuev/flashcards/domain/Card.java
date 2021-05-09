package com.gitlab.sszuev.flashcards.domain;

import java.util.Collection;
import java.util.stream.Stream;

/**
 * Describes a card entity.
 * <p>
 * Created by @ssz on 01.05.2021.
 */
public class Card extends WithText {
    private String transcription;
    private PartOfSpeech partOfSpeech;
    private Collection<Translation> translations;
    private Collection<Example> examples;
    private Status status;
    private String details;

    public void setTranslations(Collection<Translation> translations) {
        this.translations = translations;
    }

    public void setExamples(Collection<Example> examples) {
        this.examples = examples;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getTranscription() {
        return transcription;
    }

    public void setTranscription(String transcription) {
        this.transcription = transcription;
    }

    public PartOfSpeech getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(PartOfSpeech partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    public Stream<Translation> translations() {
        return translations.stream();
    }

    public Stream<Example> examples() {
        return examples.stream();
    }
}
