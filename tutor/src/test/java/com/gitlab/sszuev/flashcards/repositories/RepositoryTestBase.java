package com.gitlab.sszuev.flashcards.repositories;

import com.gitlab.sszuev.flashcards.App;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by @ssz on 16.05.2021.
 */
@Transactional
@SpringBootTest(classes = {App.class})
@ActiveProfiles(profiles = "itest")
@AutoConfigureTestEntityManager
abstract class RepositoryTestBase {
    protected static final String TEST_DICTIONARY_NAME = "Weather";
    protected static final long TEST_DICTIONARY_ID = 1L;
    protected static final List<Long> TEST_CARD_IDS = List.of(1L, 2L);

    @Autowired
    protected TestEntityManager tem;
    @Autowired
    protected DictionaryRepository dictionaryRepository;
    @Autowired
    protected CardRepository cardRepository;

    protected Statistics prepareStatistics() {
        Statistics res = tem.getEntityManager().getEntityManagerFactory().unwrap(SessionFactory.class).getStatistics();
        res.setStatisticsEnabled(true);
        res.clear();
        return res;
    }
}
