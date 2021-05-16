package com.gitlab.sszuev.flashcards.dao;

import com.gitlab.sszuev.flashcards.domain.Status;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by @ssz on 16.05.2021.
 */
public class CardRepositoryTest extends RepositoryTestBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(CardRepositoryTest.class);

    private static final long TEST_DICTIONARY_ID = 1L;
    private static final List<Long> TEST_CARD_IDS = List.of(1L, 2L);

    @Autowired
    private CardRepository repository;

    @Test
    public void testFindByDictionaryAndStatusIn() {
        Statistics statistics = prepareStatistics();
        Set<Long> ids = new HashSet<>();
        repository.streamByDictionaryIdAndStatusIn(TEST_DICTIONARY_ID, List.of(Status.NEW, Status.IN_PROCESS))
                .forEach(card -> {
                    long id = card.getID();
                    Status st = card.getStatus();
                    long dicId = card.getDictionary().getID();
                    LOGGER.info("id={}, text='{}', status='{}', translations=[{}], examples=[{}]",
                            id, card.getText(), st,
                            card.translations().count(), card.examples().count());
                    Assertions.assertTrue(ids.add(id), "Duplicate id=" + id);
                    Assertions.assertNotEquals(Status.LEARNED, st);
                    Assertions.assertEquals(TEST_DICTIONARY_ID, dicId);
                });
        Assertions.assertEquals(2, statistics.getPrepareStatementCount());
    }

    @Test
    public void testFindByIdIn() {
        Statistics statistics = prepareStatistics();
        repository.streamByIdIn(TEST_CARD_IDS)
                .forEach(card -> LOGGER.info("id={}, text='{}'", card.getID(), card.getText()));
        Assertions.assertEquals(1, statistics.getPrepareStatementCount());
    }
}
