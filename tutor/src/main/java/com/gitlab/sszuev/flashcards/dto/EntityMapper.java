package com.gitlab.sszuev.flashcards.dto;

import com.gitlab.sszuev.flashcards.domain.Card;
import com.gitlab.sszuev.flashcards.domain.Meaning;
import com.gitlab.sszuev.flashcards.service.SoundService;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by @ssz on 02.05.2021.
 */
@Component
public class EntityMapper {
    private final SoundService soundService;

    public EntityMapper(SoundService service) {
        this.soundService = Objects.requireNonNull(service);
    }

    public CardRecord toRecord(Card card) {
        String word = card.getText();
        String translations = card.meanings().flatMap(Meaning::translations)
                .map(x -> x.getText()).collect(Collectors.joining(", "));
        return new CardRecord(word, translations, soundService.getResourceName(word));
    }
}
