package com.gitlab.sszuev.flashcards.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitlab.sszuev.flashcards.domain.Card;
import com.gitlab.sszuev.flashcards.domain.Language;
import com.gitlab.sszuev.flashcards.domain.Status;
import com.gitlab.sszuev.flashcards.service.SoundService;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by @ssz on 02.05.2021.
 */
@Component
public class EntityMapper {
    private static final TypeReference<Map<Stage, List<Boolean>>> DB_DETAILS_TYPE_REFERENCE = new TypeReference<>() {
    };

    private final ObjectMapper mapper;
    private final SoundService speaker;

    public EntityMapper(SoundService service, ObjectMapper mapper) {
        this.speaker = Objects.requireNonNull(service);
        this.mapper = Objects.requireNonNull(mapper);
    }

    public CardResource createResource(Card card, Language lang) {
        String word = card.getText();
        String translations = card.translations().map(x -> x.getText()).collect(Collectors.joining(", "));
        int answered = card.getStatus() == Status.LEARNED ? 0 : Optional.ofNullable(card.getAnswered()).orElse(0);
        return new CardResource(card.getID(),
                word, translations, speaker.getResourceName(word, lang.name()), answered, Map.of());
    }

    public Map<Stage, List<Boolean>> readDetailsAsMap(Card card) {
        String details = card.getDetails();
        if (details == null || !(details.startsWith("{") && details.endsWith("}"))) {
            return new HashMap<>();
        }
        return readDetailsAsMap(details);
    }

    private Map<Stage, List<Boolean>> readDetailsAsMap(String details) {
        try {
            return mapper.readValue(details, DB_DETAILS_TYPE_REFERENCE);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Can't parse '" + details + "'", e);
        }
    }

    public String writeDetailsAsString(Map<Stage, List<Boolean>> details) {
        try {
            return mapper.writeValueAsString(details);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Can't convert " + details + " to string", e);
        }
    }
}
