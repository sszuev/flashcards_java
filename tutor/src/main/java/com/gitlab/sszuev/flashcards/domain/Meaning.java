package com.gitlab.sszuev.flashcards.domain;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Created by @ssz on 01.05.2021.
 */
public class Meaning {
    private final String transcription;
    private final PartOfSpeech partOfSpeech;
    private final Collection<Translation> translations;
    private final Collection<Example> examples;

    public Meaning(String transcription,
                   PartOfSpeech partOfSpeech,
                   Collection<Translation> translations,
                   Collection<Example> examples) {
        this.transcription = transcription;
        this.partOfSpeech = partOfSpeech;
        this.translations = Objects.requireNonNull(translations);
        this.examples = Objects.requireNonNull(examples);
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