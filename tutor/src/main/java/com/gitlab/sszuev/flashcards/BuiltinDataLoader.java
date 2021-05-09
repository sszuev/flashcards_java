package com.gitlab.sszuev.flashcards;

import com.gitlab.sszuev.flashcards.dao.DictionaryRepository;
import com.gitlab.sszuev.flashcards.dao.UserRepository;
import com.gitlab.sszuev.flashcards.domain.Dictionary;
import com.gitlab.sszuev.flashcards.parser.LingvoParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

/**
 * To load XML (lingvo) data  from resources to database.
 * <p>
 * Created by @ssz on 09.05.2021.
 */
@Component
public class BuiltinDataLoader implements ApplicationListener<ApplicationReadyEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BuiltinDataLoader.class);

    private final ResourcePatternResolver resolver;
    private final LingvoParser parser;
    private final String dataDir;

    private final DictionaryRepository dictionaryRepository;
    private final UserRepository userRepository;

    public BuiltinDataLoader(@Value("${app.data.dir:classpath:data/*}") String dir,
                             ResourcePatternResolver resolver,
                             LingvoParser parser,
                             DictionaryRepository dictionaryRepository,
                             UserRepository userRepository) {
        this.resolver = Objects.requireNonNull(resolver);
        this.parser = Objects.requireNonNull(parser);
        this.dataDir = Objects.requireNonNull(dir);
        this.dictionaryRepository = Objects.requireNonNull(dictionaryRepository);
        this.userRepository = Objects.requireNonNull(userRepository);
    }

    @Transactional
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            Arrays.stream(resolver.getResources(dataDir)).map(parser::parse).forEach(this::save);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load data", e);
        }
    }

    private void save(Dictionary dictionary) {
        LOGGER.info("Save to db: '{}'", dictionary.getName());
        userRepository.save(dictionary.getUser());
        dictionaryRepository.save(dictionary);
    }
}
