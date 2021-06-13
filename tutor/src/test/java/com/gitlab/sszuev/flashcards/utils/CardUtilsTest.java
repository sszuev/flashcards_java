package com.gitlab.sszuev.flashcards.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.BiPredicate;

/**
 * Created by @ssz on 13.06.2021.
 */
public class CardUtilsTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(CardUtilsTest.class);

    @Test
    public void testSplitWords() {
        Assertions.assertEquals(6, CardUtils.words("a,  ew,ewere;errt,&oipuoirwe,ор43ыфю,,,q,,").count());
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
