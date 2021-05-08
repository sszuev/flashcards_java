package com.gitlab.sszuev.flashcards.service;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Created by @ssz on 08.05.2021.
 */
@Service
public class SoundService {
    private final SoundProvider provider;

    public SoundService(SoundProvider provider) {
        this.provider = Objects.requireNonNull(provider);
    }

    /**
     * Returns an resource identifier (name) that corresponds the given {@code word}.
     *
     * @param word {@code String} the word, not {@code null}
     * @return {@code String} (name of resource) or {@code null} (if no resource found by the specified word)
     */
    public String getResourceName(String word) {
        return provider.getResourceName(word);
    }

    /**
     * Returns {@code Resource} for the given resource identifier (name).
     *
     * @param name {@code String} the resource name, not {@code null}
     * @return {@link Resource}, never {@code null}
     */
    public Resource getResource(String name) {
        return provider.getResource(name);
    }
}
