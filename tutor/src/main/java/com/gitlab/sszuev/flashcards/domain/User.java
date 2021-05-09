package com.gitlab.sszuev.flashcards.domain;

import javax.persistence.*;

/**
 * Created by @ssz on 08.05.2021.
 */
@Entity
@Table(name = "users")
public class User implements HasID {
    public static final long DEFAULT_USER_ID = 42;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Override
    public Long getID() {
        return id;
    }

    @Override
    public void setID(Long id) {
        this.id = id;
    }
}
