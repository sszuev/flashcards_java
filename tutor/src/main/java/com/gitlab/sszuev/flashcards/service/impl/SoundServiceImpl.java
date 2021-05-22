package com.gitlab.sszuev.flashcards.service.impl;

import com.gitlab.sszuev.flashcards.TextToSpeechService;
import com.gitlab.sszuev.flashcards.service.SoundService;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Created by @ssz on 08.05.2021.
 */
@Service
public class SoundServiceImpl implements SoundService {
    private final TextToSpeechService provider;
    private final Charset charset = StandardCharsets.UTF_8;

    public SoundServiceImpl(TextToSpeechService provider) {
        this.provider = Objects.requireNonNull(provider);
    }

    @Override
    public String getResourceName(String word, String lang) {
        String res = provider.getResourceID(word, lang);
        return res == null ? null : UriUtils.encodePathSegment(res, charset);
    }

    @Override
    public Resource getResource(String name) {
        return provider.getResource(UriUtils.decode(name, charset));
    }

}
