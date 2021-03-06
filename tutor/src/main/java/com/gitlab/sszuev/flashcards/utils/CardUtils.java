package com.gitlab.sszuev.flashcards.utils;

import com.gitlab.sszuev.flashcards.domain.Card;
import com.gitlab.sszuev.flashcards.domain.Translation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by @ssz on 13.06.2021.
 */
public class CardUtils {

    public static Collection<Card> selectRandomNonSimilarCards(List<Card> cards, int size) {
        return CollectionUtils.trySelectUniqueRandomItems(cards, size, new Random(), CardUtils::isSimilar);
    }

    /**
     * Answers {@code true} if two cards have similar meaning.
     *
     * @param left  {@link Card}
     * @param right {@link Card}
     * @return {@code boolean}
     */
    public static boolean isSimilar(Card left, Card right) {
        Set<String> words = right.translations().flatMap(CardUtils::words).collect(Collectors.toSet());
        return left.translations().flatMap(CardUtils::words).anyMatch(words::contains);
    }

    public static Stream<String> words(Translation t) {
        return words(t.getText());
    }

    /**
     * Splits the given {@code phrase} using comma (i.e. '{@code ,}') as separator.
     * Commas inside the parentheses (e.g. "{@code (x,y)}") are not considered.
     *
     * @param phrase {@code String}, not {@code null}
     * @return a {@code Stream} of {@code String}s
     */
    public static Stream<String> words(String phrase) {
        return getWords(phrase).stream();
    }

    /**
     * Splits the given {@code phrase} using comma (i.e. '{@code ,}') as separator.
     * Commas inside the parentheses (e.g. "{@code (x,y)}") are not considered.
     *
     * @param phrase {@code String}, not {@code null}
     * @return an unmodifiable {@code List} of {@code String}s
     */
    public static List<String> getWords(String phrase) {
        String[] parts = phrase.split(",");
        ArrayList<String> res = new ArrayList<>(parts.length);
        for (int i = 0; i < parts.length; i++) {
            String pi = parts[i].trim();
            if (pi.isEmpty()) {
                continue;
            }
            if (!pi.contains("(") || pi.contains(")")) {
                res.add(pi);
                continue;
            }
            StringBuilder sb = new StringBuilder(pi);
            int j = i + 1;
            for (; j < parts.length; j++) {
                String pj = parts[j].trim();
                if (pj.isEmpty()) {
                    continue;
                }
                sb.append(", ").append(pj);
                if (pj.contains(")")) {
                    break;
                }
            }
            if (sb.lastIndexOf(")") == -1) {
                res.add(pi);
                continue;
            }
            res.add(sb.toString());
            i = j;
        }
        res.trimToSize();
        return Collections.unmodifiableList(res);
    }
}
