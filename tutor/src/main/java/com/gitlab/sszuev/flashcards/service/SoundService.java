package com.gitlab.sszuev.flashcards.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Created by @ssz on 08.05.2021.
 */
@Service
public class SoundService {
    private static final String DIR = "sounds";

    private final ResourcePatternResolver resolver;

    public SoundService(ResourcePatternResolver resolver) {
        this.resolver = Objects.requireNonNull(resolver);
    }

    /**
     * Returns an resource identifier (name) that corresponds the given {@code word}.
     *
     * @param word {@code String} the word, not {@code null}
     * @return {@code String} (name of resource) or {@code null} (if no resource found by the specified word)
     */
    public String getResourceName(String word) {
        Resource res = resolver.getResource(String.format("classpath:%s/%s.wav", DIR, word.replace(" ", "_")));
        return res.exists() ? res.getFilename() : null;
    }

    /**
     * Returns {@code Resource} for the given resource identifier (name).
     *
     * @param name {@code String} the resource name, not {@code null}
     * @return {@link Resource}, never {@code null}
     */
    public Resource getResource(String name) {
        return new ClassPathResource(DIR + "/" + Objects.requireNonNull(name));
    }
}
