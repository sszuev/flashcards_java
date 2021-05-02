package com.gitlab.sszuev.flashcards.dto;

import com.gitlab.sszuev.flashcards.domain.Card;
import com.gitlab.sszuev.flashcards.domain.Meaning;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Created by @ssz on 02.05.2021.
 */
@Component
public class EntityMapper {

    public CardRecord toRecord(Card card) {
        String word = card.getText();
        String translations = card.meanings().flatMap(Meaning::translations)
                .map(x -> x.getText()).collect(Collectors.joining(", "));
        return new CardRecord(word, translations);
    }
}
