package com.gitlab.sszuev.flashcards.domain;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Describes the dictionary - a collection of cards.
 * <p>
 * Created by @ssz on 29.04.2021.
 */
public class Dictionary {
    private final String name;
    private final User user;
    private final Language srcLanguage;
    private final Language dstLanguage;
    private final List<Card> cards;

    public Dictionary(User user, String name, Language srcLanguage, Language dstLanguage, List<Card> cards) {
        this.name = Objects.requireNonNull(name);
        this.user = Objects.requireNonNull(user);
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

    public Card getCard(int index) {
        return cards.get(index);
    }

    public long getCardsCount() {
        return cards.size();
    }

    public User getUser() {
        return user;
    }
}
