package com.gitlab.sszuev.flashcards.domain;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Describes the dictionary - a collection of cards.
 *
 * Created by @ssz on 29.04.2021.
 */
public class Dictionary {
    private final String name;
    private final Language srcLanguage;
    private final Language dstLanguage;
    private final Collection<Card> cards;

    public Dictionary(String name, Language srcLanguage, Language dstLanguage, Collection<Card> cards) {
        this.name = Objects.requireNonNull(name);
        this.srcLanguage = Objects.requireNonNull(srcLanguage);
        this.dstLanguage = Objects.requireNonNull(dstLanguage);
        this.cards = Objects.requireNonNull(cards);
    }

    public String getName() {
        return name;
    }

    public Language getSourceLanguage() {
        return srcLanguage;
    }

    public Language getTargetLanguage() {
        return dstLanguage;
    }

    public Stream<Card> cards() {
        return cards.stream();
    }
}
