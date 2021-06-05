package com.gitlab.sszuev.flashcards.services;

import com.gitlab.sszuev.flashcards.Compounder;
import com.gitlab.sszuev.flashcards.TextToSpeechService;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * An implementation, that combines {@link LocalResourceTTSImpl} and {@link CompositeTTSImpl}.
 * TODO: add in-memory cache for resources.
 * <p>
 * Created by @ssz on 22.05.2021.
 */
@Primary
@Service
public class CompositeTTSImpl implements TextToSpeechService {
    private final List<Map.Entry<String, TextToSpeechService>> services;
    private final Compounder compounder;

    public CompositeTTSImpl(LocalResourceTTSImpl localService, VoiceRSSClientTTSImpl externalService, Compounder compounder) {
        this.compounder = Objects.requireNonNull(compounder);
        this.services = List.of(
                Map.entry("local", Objects.requireNonNull(localService)),
                Map.entry("external", Objects.requireNonNull(externalService))
        );
    }

    @Override
    public String getResourceID(String text, String language, String... options) {
        for (Map.Entry<String, TextToSpeechService> service : services) {
            String res = service.getValue().getResourceID(text, language, options);
            if (res != null) {
                return compounder.create(service.getKey(), res);
            }
        }
        return null;
    }

    @Override
    public Resource getResource(String id) {
        for (Map.Entry<String, TextToSpeechService> service : services) {
            if (compounder.hasFirst(id, service.getKey())) {
                return service.getValue().getResource(compounder.getRest(id));
            }
        }
        throw new IllegalArgumentException("No resource found for identifier: " + id);
    }

}
