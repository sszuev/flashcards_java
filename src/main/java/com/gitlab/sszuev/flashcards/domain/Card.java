package com.gitlab.sszuev.flashcards.domain;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Created by @ssz on 29.04.2021.
 */
public class Card extends WithText {
    private final Collection<Meaning> meanings;

    public Card(String word, Collection<Meaning> meanings) {
        super(word);
        this.meanings = Objects.requireNonNull(meanings);
    }

    public Stream<Meaning> meanings() {
        return meanings.stream();
    }
}
