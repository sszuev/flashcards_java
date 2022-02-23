package com.gitlab.sszuev.flashcards.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Created by @ssz on 15.05.2021.
 */
public record CardRequest(long id, Map<Stage, Boolean> details) {
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public CardRequest(@JsonProperty("id") long id, @JsonProperty("details") Map<Stage, Boolean> details) {
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
