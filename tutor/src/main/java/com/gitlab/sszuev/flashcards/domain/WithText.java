package com.gitlab.sszuev.flashcards.domain;

/**
 * Created by @ssz on 02.05.2021.
 */
abstract class WithText {
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
