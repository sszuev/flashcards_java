package com.gitlab.sszuev.flashcards.domain;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Describes a card entity.
 * <p>
 * Created by @ssz on 01.05.2021.
 */
public class Card extends WithText {
    private final String transcription;
    private final PartOfSpeech partOfSpeech;
    private final Collection<Translation> translations;
    private final Collection<Example> examples;
    private final Status status;
    private final String details;

    public Card(String word,
                String transcription,
                PartOfSpeech partOfSpeech,
                Collection<Translation> translations,
                Collection<Example> examples,
                Status status,
                String details) {
        super(word);
        this.transcription = transcription;
        this.partOfSpeech = partOfSpeech;
        this.translations = Objects.requireNonNull(translations);
        this.examples = Objects.requireNonNull(examples);
        this.status = status;
        this.details = details;
    }

    public Status getStatus() {
        return status;
    }

    public String getDetails() {
        return details;
    }

    public String getTranscription() {
        return transcription;
    }

    public PartOfSpeech getPartOfSpeech() {
        return partOfSpeech;
    }

    public Stream<Translation> translations() {
        return translations.stream();
    }

    public Stream<Example> examples() {
        return examples.stream();
    }
}
