package com.gitlab.sszuev.flashcards;

import com.gitlab.sszuev.flashcards.domain.Card;
import com.gitlab.sszuev.flashcards.domain.Dictionary;
import com.gitlab.sszuev.flashcards.domain.Meaning;

import java.util.stream.Collectors;

/**
 * Created by @ssz on 02.05.2021.
 */
public class TestUtils {

    public static String format(Dictionary d) {
        return String.format("%s[%s => %s]", d.getName(), d.getSourceLanguage(), d.getTargetLanguage());
    }

    public static String format(Card c) {
        return String.format("%s => %s", c.getText(),
                c.meanings().flatMap(Meaning::translations).map(x -> x.getText()).collect(Collectors.joining(", ")));
    }
}
