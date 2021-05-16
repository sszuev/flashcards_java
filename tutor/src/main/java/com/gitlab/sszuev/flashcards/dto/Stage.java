package com.gitlab.sszuev.flashcards.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Objects;

/**
 * Describes all supported testing stages.
 * <p>
 * Created by @ssz on 10.05.2021.
 */
public enum Stage {
    MOSAIC,
    OPTIONS,
    WRITING,
    SELF_TEST {
        @Override
        public String getID() {
            return "self-test";
        }
    },
    ;

    @JsonValue
    public String getID() {
        return name().toLowerCase();
    }

    @JsonCreator
    public static Stage of(String id) {
        for (Stage s : values()) {
            if (Objects.equals(id, s.getID())) return s;
        }
        throw new IllegalArgumentException();
    }
}
