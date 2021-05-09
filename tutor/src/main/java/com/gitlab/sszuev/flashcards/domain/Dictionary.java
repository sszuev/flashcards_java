package com.gitlab.sszuev.flashcards.domain;

import javax.persistence.*;
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
    @Column(name = "source_lang", nullable = false)
    private String srcLang;
    @Column(name = "target_lang", nullable = false)
    private String dstLang;
    @OneToMany(targetEntity = Card.class
            , mappedBy = "dictionary"
            , orphanRemoval = true
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
        return Language.fromString(srcLang);
    }

    public void setSourceLanguage(Language srcLang) {
        this.srcLang = Language.toString(srcLang);
    }

    public Language getTargetLanguage() {
        return Language.fromString(dstLang);
    }

    public void setTargetLanguage(Language dstLang) {
        this.dstLang = Language.toString(dstLang);
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
