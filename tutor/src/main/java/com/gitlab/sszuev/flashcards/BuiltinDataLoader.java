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
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileNotFoundException;
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

    private final Resource[] resources;
    private final LingvoParser parser;

    private final DictionaryRepository dictionaryRepository;
    private final UserRepository userRepository;

    public BuiltinDataLoader(@Value("${app.data.dir:classpath:data/*}") String dir,
                             ResourcePatternResolver resolver,
                             LingvoParser parser,
                             DictionaryRepository dictionaryRepository,
                             UserRepository userRepository) {
        this.resources = readResources(Objects.requireNonNull(resolver), Objects.requireNonNull(dir));
        this.parser = Objects.requireNonNull(parser);
        this.dictionaryRepository = Objects.requireNonNull(dictionaryRepository);
        this.userRepository = Objects.requireNonNull(userRepository);
    }

    private static Resource[] readResources(ResourcePatternResolver resolver, String dir) {
        try {
            return resolver.getResources(dir);
        } catch (FileNotFoundException ex) {
            LOGGER.warn("Directory <" + dir + "> does not exist");
            return new Resource[0];
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read resources from <" + dir + ">.", e);
        }
    }

    @Transactional
    @Override
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
        if (resources.length == 0) {
            LOGGER.info("No data found to upload.");
            return;
        }
        Arrays.stream(resources).filter(Resource::isReadable).map(this::parse).forEach(this::save);
        LOGGER.info("Uploading is completed.");
    }

    private Dictionary parse(Resource resource) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Parse resource: {}", resource);
        }
        return parser.parse(resource);
    }

    private void save(Dictionary dictionary) {
        LOGGER.info("Upload: '{}'", dictionary.getName());
        userRepository.save(dictionary.getUser());
        dictionaryRepository.save(dictionary);
    }
}
