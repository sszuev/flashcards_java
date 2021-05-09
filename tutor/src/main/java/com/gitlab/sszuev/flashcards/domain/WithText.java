package com.gitlab.sszuev.flashcards.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * Created by @ssz on 02.05.2021.
 */
@MappedSuperclass
abstract class WithText {
    @Column(name = "text", nullable = false)
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
