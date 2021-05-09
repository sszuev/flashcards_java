package com.gitlab.sszuev.flashcards.domain;

import javax.persistence.*;

/**
 * Created by @ssz on 09.05.2021.
 */
@MappedSuperclass
abstract class CardPart extends WithText implements HasID {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    @Override
    public Long getID() {
        return id;
    }

    @Override
    public void setID(Long id) {
        this.id = id;
    }
}
