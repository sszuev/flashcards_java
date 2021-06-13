package com.gitlab.sszuev.flashcards.utils;

import com.gitlab.sszuev.flashcards.domain.Card;
import com.gitlab.sszuev.flashcards.domain.Translation;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by @ssz on 13.06.2021.
 */
public class CardUtils {
    private static final String TRANSLATION_SEPARATOR = "\\s*,\\s*";
    private static final BiPredicate<Card, Card> SIMILAR = CardUtils::isSimilar;

    public static Collection<Card> selectRandomNonSimilarCards(List<Card> cards, int size) {
        return CollectionUtils.trySelectUniqueRandomItems(cards, size, new Random(), SIMILAR);
    }

    public static boolean isSimilar(Card left, Card right) {
        Set<String> words = right.translations().flatMap(CardUtils::words).collect(Collectors.toSet());
        return left.translations().flatMap(CardUtils::words).anyMatch(words::contains);
    }

    public static Stream<String> words(Translation t) {
        return words(t.getText());
    }

    public static Stream<String> words(String phrase) {
        return Arrays.stream(phrase.split(TRANSLATION_SEPARATOR)).filter(x -> !x.isBlank());
    }

}
