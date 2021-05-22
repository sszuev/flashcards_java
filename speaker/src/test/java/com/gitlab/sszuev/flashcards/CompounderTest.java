package com.gitlab.sszuev.flashcards;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Created by @ssz on 22.05.2021.
 */
@SpringBootTest(classes = Compounder.class)
public class CompounderTest {
    @Autowired
    private Compounder helper;

    @Test
    public void testFirstId() {
        String first = "aaa";
        String rest = "aa:bbb:ccc";
        Assertions.assertEquals(first, helper.getFirst(first + ":" + rest));
    }

    @Test
    public void testGetRestId() {
        String first = "aaa";
        String rest = "aa:bbb:ccc";
        Assertions.assertEquals(rest, helper.getRest(first + ":" + rest));
    }
}
