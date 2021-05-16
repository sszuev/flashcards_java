package com.gitlab.sszuev.flashcards.dao;

import com.gitlab.sszuev.flashcards.domain.Dictionary;
import com.gitlab.sszuev.flashcards.domain.Status;
import com.gitlab.sszuev.flashcards.domain.User;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Integration acceptance test.
 * Created by @ssz on 02.05.2021.
 */
public class DictionaryRepositoryTest extends RepositoryTestBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(DictionaryRepositoryTest.class);

    private static final String TEST_DICTIONARY_NAME = "Weather";

    @Autowired
    private DictionaryRepository repository;

    @Test
    public void testFindByUserAndName() {
        Statistics statistics = prepareStatistics();
        Dictionary dic = repository.findByUserIdAndName(User.DEFAULT_USER_ID, TEST_DICTIONARY_NAME)
                .orElseThrow(AssertionError::new);
        LOGGER.info("Dictionary: {} ({} => {})", dic.getName(), dic.getSourceLanguage(), dic.getTargetLanguage());
        Assertions.assertEquals(1, statistics.getPrepareStatementCount());
        Assertions.assertEquals(65, dic.cards().count());
        dic.cards().forEach(c -> {
            LOGGER.info("{} => {}({})", c.getText(),
                    c.translations().map(x -> x.getText()).collect(Collectors.joining(", ")),
                    c.examples().map(x -> x.getText()).collect(Collectors.joining(", ")));
            Assertions.assertEquals(Status.NEW, c.getStatus());
        });
        Assertions.assertEquals(4, statistics.getPrepareStatementCount());
    }

    @Test
    public void testListByUser() {
        Statistics statistics = prepareStatistics();
        List<Dictionary> list = repository.streamAllByUserId(User.DEFAULT_USER_ID).collect(Collectors.toList());
        Assertions.assertEquals(1, list.size());
        Assertions.assertEquals(TEST_DICTIONARY_NAME, list.get(0).getName());
        Assertions.assertEquals(1, statistics.getPrepareStatementCount());
    }
}
