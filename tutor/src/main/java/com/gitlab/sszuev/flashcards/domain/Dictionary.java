package com.gitlab.sszuev.flashcards.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;
import java.util.stream.Stream;

/**
 * Describes the dictionary - a collection of cards.
 * <p>
 * Created by @ssz on 29.04.2021.
 */
@Entity
@Table(name = "dictionaries")
public class Dictionary implements HasID {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "source_lang", nullable = false)
    private Language sourceLang;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "target_lang", nullable = false)
    private Language targetLang;
    @OneToMany(targetEntity = Card.class
            , mappedBy = "dictionary"
            , orphanRemoval = true
            , fetch = FetchType.LAZY
            , cascade = CascadeType.ALL)
    private List<Card> cards;

    @Override
    public Long getID() {
        return id;
    }

    @Override
    public void setID(Long id) {
        this.id = id;
    }

    public Language getSourceLanguage() {
        return sourceLang;
    }

    public void setSourceLanguage(Language srcLang) {
        this.sourceLang = srcLang;
    }

    public Language getTargetLanguage() {
        return targetLang;
    }

    public void setTargetLanguage(Language dstLang) {
        this.targetLang = dstLang;
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
