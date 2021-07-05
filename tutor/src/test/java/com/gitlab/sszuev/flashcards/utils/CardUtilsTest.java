package com.gitlab.sszuev.flashcards.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

/**
 * Created by @ssz on 13.06.2021.
 */
public class CardUtilsTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(CardUtilsTest.class);

    @Test
    public void testSplitWords() {
        assertSplitWords(0, " ");
        assertSplitWords(1, "a.  bb.xxx;yyy");
        assertSplitWords(6, "a,  ew,ewere;errt,&oipuoirwe,ор43ыфю,,,q,,");
        assertSplitWords(10, "mmmmmmmm, uuuuuu, uuu (sss, xzxx, aaa), ddd, sss, q, www,ooo , ppp, sss. in zzzzz");
        assertSplitWords(3, "s s s s (smth l.), d (smth=d., smth=g) x, (&,?)x y");
    }

    private void assertSplitWords(int expectedSize, String givenString) {
        List<String> actual1 = CardUtils.words(givenString).collect(Collectors.toList());
        LOGGER.info("String({}): '{}'.", expectedSize, givenString);
        LOGGER.info("Parts({}): {}.", actual1.size(), actual1.stream().collect(Collectors.joining("', '", "'", "'")));
        Assertions.assertEquals(expectedSize, actual1.size());
        actual1.forEach(this::assertPhrasePart);
        Assertions.assertEquals(expectedSize, CardUtils.getWords(givenString).size());
        List<String> actual2 = CardUtils.words(givenString).collect(Collectors.toList());
        Assertions.assertEquals(actual1, actual2);
    }

    private void assertPhrasePart(String s) {
        Assertions.assertFalse(s.isEmpty());
        Assertions.assertFalse(s.startsWith(" "));
        Assertions.assertFalse(s.endsWith(" "));
        if (!s.contains("(") || !s.contains(")")) {
            Assertions.assertFalse(s.contains(","), "For string '" + s + "'");
        }
    }

    @Test
    public void testSelectRandomUnique() {
        Random r = new Random();
        BiPredicate<String, String> similar = String::equalsIgnoreCase;
        List<String> data = List.of("a", "b", "c", "D", "d", "C", "A");
        Collection<String> res1 = CollectionUtils.trySelectUniqueRandomItems(data, 3, r, similar);
        LOGGER.info("Result(1): {}", res1);
        Assertions.assertEquals(3, res1.size());

        Collection<String> res2 = CollectionUtils.trySelectUniqueRandomItems(data, 4, r, similar);
        LOGGER.info("Result(2): {}", res2);
        Assertions.assertEquals(4, res2.size());

        Collection<String> res3 = CollectionUtils.trySelectUniqueRandomItems(data, 7, r, similar);
        LOGGER.info("Result(3): {}", res3);
        Assertions.assertEquals(7, res3.size());

        Collection<String> res4 = CollectionUtils.trySelectUniqueRandomItems(data, 2, r, (a, b) -> true);
        LOGGER.info("Result(4): {}", res4);
        Assertions.assertEquals(2, res4.size());
    }
}
