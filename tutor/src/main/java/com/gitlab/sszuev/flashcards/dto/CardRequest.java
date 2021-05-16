package com.gitlab.sszuev.flashcards.dto;

import java.util.Map;

/**
 * Created by @ssz on 15.05.2021.
 */
public class CardRequest {
    private final long id;
    private final Map<Stage, Boolean> details;

    public CardRequest(long id, Map<Stage, Boolean> details) {
        this.id = id;
        this.details = details;
    }

    public long getId() {
        return id;
    }

    public Map<Stage, Boolean> getDetails() {
        return details;
    }
}
