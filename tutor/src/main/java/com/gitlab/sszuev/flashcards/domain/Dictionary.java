package com.gitlab.sszuev.flashcards.domain;

import java.util.List;
import java.util.stream.Stream;

/**
 * Describes the dictionary - a collection of cards.
 * <p>
 * Created by @ssz on 29.04.2021.
 */
public class Dictionary {
    private String name;
    private User user;
    private Language srcLanguage;
    private Language dstLanguage;
    private List<Card> cards;

    public Language getSrcLanguage() {
        return srcLanguage;
    }

    public void setSrcLanguage(Language srcLanguage) {
        this.srcLanguage = srcLanguage;
    }

    public Language getDstLanguage() {
        return dstLanguage;
    }

    public void setDstLanguage(Language dstLanguage) {
        this.dstLanguage = dstLanguage;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public void setUser(User user) {
        this.user = user;
    }
}
