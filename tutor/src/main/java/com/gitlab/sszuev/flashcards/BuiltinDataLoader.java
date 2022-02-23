package com.gitlab.sszuev.flashcards;

import com.gitlab.sszuev.flashcards.domain.Dictionary;
import com.gitlab.sszuev.flashcards.domain.Language;
import com.gitlab.sszuev.flashcards.domain.User;
import com.gitlab.sszuev.flashcards.parser.LingvoParser;
import com.gitlab.sszuev.flashcards.repositories.DictionaryRepository;
import com.gitlab.sszuev.flashcards.repositories.LanguageRepository;
import com.gitlab.sszuev.flashcards.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * To load XML (lingvo) data  from resources to database.
 * <p>
 * Created by @ssz on 09.05.2021.
 */
@ConditionalOnProperty(name = "app.loader.enabled", havingValue = "true")
@Component
public class BuiltinDataLoader implements ApplicationListener<ApplicationReadyEvent> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BuiltinDataLoader.class);

    private final Resource[] resources;
    private final LingvoParser parser;

    private final LanguageRepository languageRepository;
    private final DictionaryRepository dictionaryRepository;
    private final UserRepository userRepository;

    public BuiltinDataLoader(@Value("${app.data.dir:classpath:data/*}") String dir,
                             ResourcePatternResolver resolver,
                             LingvoParser parser,
                             LanguageRepository languageRepository,
                             DictionaryRepository dictionaryRepository,
                             UserRepository userRepository) {
        this.resources = readResources(Objects.requireNonNull(resolver), Objects.requireNonNull(dir));
        this.parser = Objects.requireNonNull(parser);
        this.dictionaryRepository = Objects.requireNonNull(dictionaryRepository);
        this.languageRepository = Objects.requireNonNull(languageRepository);
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

    @Override
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
        if (resources.length == 0) {
            LOGGER.info("No data found to upload.");
            return;
        }
        List<Dictionary> dictionaries = Arrays.stream(resources)
                .filter(Resource::isReadable).map(this::parse)
                .collect(Collectors.toList());
        saveAll(dictionaries);
        LOGGER.info("Uploading is completed.");
    }

    private Dictionary parse(Resource resource) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Parse resource: {}", resource);
        }
        return parser.parse(resource);
    }

    @Transactional
    public void saveAll(Collection<Dictionary> dictionaries) {
        // refresh user links
        dictionaries.forEach(d -> {
            d.setUser(saveUserIfNeeded(d.getUser()));
            d.setSourceLanguage(saveLanguageIfNeeded(d.getSourceLanguage()));
            d.setTargetLanguage(saveLanguageIfNeeded(d.getTargetLanguage()));
        });

        LOGGER.info("Upload: '{}'", dictionaries.stream().map(Dictionary::getName).collect(Collectors.joining(",")));
        dictionaryRepository.saveAll(dictionaries);
    }

    private User saveUserIfNeeded(User given) {
        return userRepository.findByLogin(given.getLogin()).orElseGet(() -> {
            LOGGER.info("Create user: '{}'", given.getLogin());
            return userRepository.save(given);
        });
    }

    private Language saveLanguageIfNeeded(Language language) {
        return languageRepository.findById(language.getID()).orElseGet(() -> {
            LOGGER.info("Create language: '{}'", language.getID());
            return languageRepository.save(language);
        });
    }
}
