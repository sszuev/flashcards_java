package com.gitlab.sszuev.flashcards.domain;

/**
 * Created by @ssz on 08.05.2021.
 */
public final class User {
    public static final User DEFAULT = new User(42L);
    private final Long id;

    public User(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
