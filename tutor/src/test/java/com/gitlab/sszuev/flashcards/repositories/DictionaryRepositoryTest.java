package com.gitlab.sszuev.flashcards.repositories;

import com.gitlab.sszuev.flashcards.domain.Dictionary;
import com.gitlab.sszuev.flashcards.domain.User;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Integration acceptance test.
 * Created by @ssz on 02.05.2021.
 */
public class DictionaryRepositoryTest extends RepositoryTestBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(DictionaryRepositoryTest.class);

    @Test
    public void testListCards() {
        Statistics statistics = prepareStatistics();
        Dictionary dic = dictionaryRepository.streamAll()
                .filter(x -> Objects.equals(User.SYSTEM_USER.getLogin(), x.getUser().getLogin()))
                .filter(x -> Objects.equals(TEST_DICTIONARY_NAME, x.getName()))
                .findFirst()
                .orElseThrow(AssertionError::new);
        Assertions.assertEquals(2, statistics.getPrepareStatementCount());
        LOGGER.info("Dictionary: {} ({} => {})", dic.getName(), dic.getSourceLanguage(), dic.getTargetLanguage());
        Assertions.assertEquals(3, statistics.getPrepareStatementCount());
        Assertions.assertEquals(65, dic.cards().count());
        dic.cards().forEach(c -> {
            LOGGER.info("{} => {}({})", c.getText(),
                    c.translations().map(x -> x.getText()).collect(Collectors.joining(", ")),
                    c.examples().map(x -> x.getText()).collect(Collectors.joining(", ")));
            Assertions.assertNull(c.getAnswered());
        });
        Assertions.assertEquals(6, statistics.getPrepareStatementCount());
    }

    @Test
    public void testListByUser() {
        Statistics statistics = prepareStatistics();
        List<Dictionary> list = dictionaryRepository.streamAllByUserId(1).toList();
        Assertions.assertEquals(2, list.size());
        Set<String> names = list.stream().map(Dictionary::getName).collect(Collectors.toSet());
        Assertions.assertEquals(2, names.size());
        Assertions.assertTrue(names.contains(TEST_DICTIONARY_NAME));
        Assertions.assertEquals(1, statistics.getPrepareStatementCount());
    }
}
