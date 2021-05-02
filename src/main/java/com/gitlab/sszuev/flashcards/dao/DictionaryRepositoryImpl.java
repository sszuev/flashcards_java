package com.gitlab.sszuev.flashcards.dao;

import com.gitlab.sszuev.flashcards.domain.Dictionary;
import com.gitlab.sszuev.flashcards.parser.LingvoParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This impl just loads data from resource dir (as a temporary solution).
 * <p>
 * Created by @ssz on 02.05.2021.
 */
@Component
public class DictionaryRepositoryImpl implements DictionaryRepository {
    private final Map<String, Dictionary> data;

    public DictionaryRepositoryImpl(@Value("${app.data.dir#classpath:data/*}") String dir,
                                    ResourcePatternResolver resolver,
                                    LingvoParser parser) throws IOException {
        this.data = Arrays.stream(resolver.getResources(dir)).map(parser::parse)
                .collect(Collectors.toUnmodifiableMap(Dictionary::getName, Function.identity(), (a, b) -> {
                    throw new IllegalStateException("Duplicate dictionary " + a.getName());
                }));
    }

    @Override
    public Optional<Dictionary> findByName(String name) {
        return Optional.ofNullable(data.get(name));
    }
}
