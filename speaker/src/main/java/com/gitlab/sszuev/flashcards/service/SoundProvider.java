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
public class SoundProvider {
    private static final String DIR = "data/sounds";

    private final ResourcePatternResolver resolver;

    public SoundProvider(ResourcePatternResolver resolver) {
        this.resolver = Objects.requireNonNull(resolver);
    }

    /**
     * Returns an resource identifier (name) that corresponds the given {@code phrase}.
     *
     * @param phrase {@code String}, not {@code null}
     * @return {@code String} (name of resource) or {@code null} (if no resource found by the specified word)
     */
    public String getResourceName(String phrase) {
        Resource res = resolver.getResource(String.format("classpath:%s/%s.wav", DIR, phrase.replace(" ", "_")));
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
