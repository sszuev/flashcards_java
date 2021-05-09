package com.gitlab.sszuev.flashcards.dto;

import com.gitlab.sszuev.flashcards.domain.Card;
import com.gitlab.sszuev.flashcards.domain.Language;
import com.gitlab.sszuev.flashcards.service.SoundService;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by @ssz on 02.05.2021.
 */
@Component
public class EntityMapper {
    private final SoundService service;

    public EntityMapper(SoundService service) {
        this.service = Objects.requireNonNull(service);
    }

    public CardRecord toRecord(Card card, Language lang) {
        String word = card.getText();
        String translations = card.translations()
                .map(x -> x.getText()).collect(Collectors.joining(", "));
        return new CardRecord(word, translations, service.getResourceName(word, lang.name()));
    }
}
