package com.gitlab.sszuev.flashcards.domain;

/**
 * The technical interface for combining entities that have {@code long} id.
 * Created by @ssz on 09.05.2021.
 */
interface HasID {
    /**
     * Returns the unique identifier of the entity.
     *
     * @return {@code Long}
     */
    Long getID();

    /**
     * Sets the specified unique identifier to the entity.
     *
     * @param id {@code Long}
     */
    void setID(Long id);
}
