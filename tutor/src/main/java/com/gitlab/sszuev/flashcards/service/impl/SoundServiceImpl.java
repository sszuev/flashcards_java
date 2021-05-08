package com.gitlab.sszuev.flashcards.service.impl;

import com.gitlab.sszuev.flashcards.service.SoundProvider;
import com.gitlab.sszuev.flashcards.service.SoundService;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Created by @ssz on 08.05.2021.
 */
@Service
public class SoundServiceImpl implements SoundService {
    private final SoundProvider provider;

    public SoundServiceImpl(SoundProvider provider) {
        this.provider = Objects.requireNonNull(provider);
    }

    @Override
    public String getResourceName(String word) {
        return provider.getResourceName(word);
    }

    @Override
    public Resource getResource(String name) {
        return provider.getResource(name);
    }

}
