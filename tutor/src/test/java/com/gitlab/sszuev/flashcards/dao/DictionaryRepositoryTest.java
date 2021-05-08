package com.gitlab.sszuev.flashcards.dao;

import com.gitlab.sszuev.flashcards.TestUtils;
import com.gitlab.sszuev.flashcards.domain.Dictionary;
import com.gitlab.sszuev.flashcards.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by @ssz on 02.05.2021.
 */
@SpringBootTest
public class DictionaryRepositoryTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(DictionaryRepositoryTest.class);

    @Autowired
    private DictionaryRepository repository;

    @Test
    public void testFindByUserAndName() {
        Dictionary dic = repository.findByUserIdAndName(User.DEFAULT.getId(), "Weather")
                .orElseThrow(AssertionError::new);
        LOGGER.info("Dictionary: {}", TestUtils.format(dic));
        Assertions.assertEquals(65, dic.cards().count());
        dic.cards().forEach(card -> LOGGER.info("{}", TestUtils.format(card)));
    }

    @Test
    public void testListByUser() {
        List<Dictionary> list = repository.streamAllByUserId(User.DEFAULT.getId()).collect(Collectors.toList());
        Assertions.assertEquals(1, list.size());
        Assertions.assertEquals("Weather", list.get(0).getName());
    }
}
