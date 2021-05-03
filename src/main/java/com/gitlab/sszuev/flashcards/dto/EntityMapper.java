package com.gitlab.sszuev.flashcards.dto;

import com.gitlab.sszuev.flashcards.domain.Card;
import com.gitlab.sszuev.flashcards.domain.Meaning;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by @ssz on 02.05.2021.
 */
@Component
public class EntityMapper {
    private final ResourcePatternResolver resolver;

    public EntityMapper(ResourcePatternResolver resolver) {
        this.resolver = Objects.requireNonNull(resolver);
    }

    public CardRecord toRecord(Card card) {
        String word = card.getText();
        String translations = card.meanings().flatMap(Meaning::translations)
                .map(x -> x.getText()).collect(Collectors.joining(", "));
        Resource res = resolver.getResource("classpath:sounds/" + word.replace(" ", "_") + ".wav");
        return new CardRecord(word, translations, res.exists() ? res.getFilename() : null);
    }
}
