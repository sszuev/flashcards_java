package com.gitlab.sszuev.flashcards;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * A helper to conduct uniform work with {@code String} composite identifiers.
 * <p>
 * Created by @ssz on 22.05.2021.
 */
@Component
public class Compounder {
    private final String separator;

    @Autowired
    public Compounder() {
        this(":");
    }

    public Compounder(String separator) {
        this.separator = Objects.requireNonNull(separator);
    }

    public String getFirst(String id) {
        return validate(id).substring(0, id.indexOf(separator));
    }

    public String getRest(String id) {
        return validate(id).substring(id.indexOf(separator) + separator.length());
    }

    public String create(String first, String rest) {
        return String.format("%s%s%s", first, separator, rest);
    }

    public String validate(String id) {
        if (!isCompound(id)) {
            throw new IllegalArgumentException("Wrong identifier: <" + id + ">.");
        }
        return id;
    }

    public boolean isCompound(String id) {
        return id.contains(separator);
    }

    public boolean hasFirst(String id, String key) {
        return id.length() > key.length() + separator.length() && id.startsWith(key + separator);
    }
}
