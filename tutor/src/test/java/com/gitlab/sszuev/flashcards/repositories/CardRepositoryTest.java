package com.gitlab.sszuev.flashcards.repositories;

import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by @ssz on 16.05.2021.
 */
public class CardRepositoryTest extends RepositoryTestBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(CardRepositoryTest.class);

    @Test
    public void testFindByDictionaryAndMaxAnswers() {
        Statistics statistics = prepareStatistics();
        Set<Long> ids = new HashSet<>();
        int maxAnswers = 5;
        cardRepository.streamByDictionaryIdAndAnsweredLessThan(TEST_DICTIONARY_ID, maxAnswers)
                .forEach(card -> {
                    long id = card.getID();
                    Integer answered = card.getAnswered();
                    long dicId = card.getDictionary().getID();
                    LOGGER.info("id={}, text='{}', status='{}', translations=[{}], examples=[{}]",
                            id, card.getText(), answered,
                            card.translations().count(), card.examples().count());
                    Assertions.assertTrue(ids.add(id), "Duplicate id=" + id);
                    Assertions.assertTrue(answered == null || answered < maxAnswers);
                    Assertions.assertEquals(TEST_DICTIONARY_ID, dicId);
                });
        Assertions.assertEquals(2, statistics.getPrepareStatementCount());
    }

    @Test
    public void testFindByIdIn() {
        Statistics statistics = prepareStatistics();
        cardRepository.streamByIdIn(TEST_CARD_IDS)
                .forEach(card -> LOGGER.info("id={}, text='{}'", card.getID(), card.getText()));
        Assertions.assertEquals(1, statistics.getPrepareStatementCount());
    }
}
