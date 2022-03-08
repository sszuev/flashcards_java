package com.gitlab.sszuev.flashcards.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * Created by @ssz on 15.05.2021.
 */
public record CardUpdateResource(long id, Map<Stage, Integer> details) {
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public CardUpdateResource(@JsonProperty("id") long id, @JsonProperty("details") Map<Stage, Integer> details) {
        this.id = id;
        this.details = details;
    }
}
