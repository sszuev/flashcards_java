package com.gitlab.sszuev.flashcards.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * TODO:
 *  - 1) add data to archive (include other archives)
 *  - 2) include external tts service
 *
 * Created by @ssz on 08.05.2021.
 */
@Service
public class TextToSpeechServiceImpl implements TextToSpeechService {
    private static final String DIR = "data/sounds/en";

    private final ResourcePatternResolver resolver;

    public TextToSpeechServiceImpl(ResourcePatternResolver resolver) {
        this.resolver = Objects.requireNonNull(resolver);
    }

    @Override
    public String getResourceID(String text, String lang) {
        if (!"en".equalsIgnoreCase(lang)) {
            // TODO: handle lang
            throw new UnsupportedOperationException("TODO");
        }
        Resource res = resolver.getResource(String.format("classpath:%s/%s.wav", DIR, text.replace(" ", "_")));
        return res.exists() ? res.getFilename() : null;
    }

    @Override
    public Resource getResource(String name) {
        Resource res = new ClassPathResource(DIR + "/" + Objects.requireNonNull(name));
        if (res.exists()) {
            return res;
        }
        throw new IllegalArgumentException("Wrong path identifier: " + name);
    }

}
