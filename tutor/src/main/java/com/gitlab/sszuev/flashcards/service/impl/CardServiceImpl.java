package com.gitlab.sszuev.flashcards.service.impl;

import com.gitlab.sszuev.flashcards.dao.DictionaryRepository;
import com.gitlab.sszuev.flashcards.domain.Dictionary;
import com.gitlab.sszuev.flashcards.domain.User;
import com.gitlab.sszuev.flashcards.dto.CardRecord;
import com.gitlab.sszuev.flashcards.dto.EntityMapper;
import com.gitlab.sszuev.flashcards.service.CardService;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * Created by @ssz on 08.05.2021.
 */
@Service
public class CardServiceImpl implements CardService {
    private final DictionaryRepository repository;
    private final EntityMapper mapper;

    public CardServiceImpl(DictionaryRepository repository, EntityMapper mapper) {
        this.repository = Objects.requireNonNull(repository);
        this.mapper = Objects.requireNonNull(mapper);
    }

    @Override
    public CardRecord getCard(String dictionaryName, Integer cardIndex) {
        int i = cardIndex == null ? 0 : cardIndex;
        Dictionary dic = repository.findByUserIdAndName(User.DEFAULT_USER_ID, dictionaryName).orElseThrow(IllegalArgumentException::new);
        long count = dic.getCardsCount();
        if (count == 0 || i >= count) {
            return null;
        }
        return mapper.toRecord(dic.getCard(i), dic.getSourceLanguage());
    }

    @Override
    public Stream<String> dictionaries() {
        return repository.streamAllByUserId(User.DEFAULT_USER_ID).map(Dictionary::getName);
    }
}
