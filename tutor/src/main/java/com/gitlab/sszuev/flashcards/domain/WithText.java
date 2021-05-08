package com.gitlab.sszuev.flashcards.domain;

import java.util.Objects;

/**
 * Created by @ssz on 02.05.2021.
 */
abstract class WithText {
    private final String text;

    public WithText(String text) {
        this.text = Objects.requireNonNull(text);
    }

    public String getText() {
        return text;
    }
}
