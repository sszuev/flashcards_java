package com.gitlab.sszuev.flashcards.dao;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by @ssz on 16.05.2021.
 */
@Transactional
@SpringBootTest
@AutoConfigureTestEntityManager
abstract class RepositoryTestBase {
    @Autowired
    protected TestEntityManager tem;

    protected Statistics prepareStatistics() {
        Statistics res = tem.getEntityManager().getEntityManagerFactory().unwrap(SessionFactory.class).getStatistics();
        res.setStatisticsEnabled(true);
        res.clear();
        return res;
    }
}
