package com.gitlab.sszuev.flashcards.service.impl;

import com.gitlab.sszuev.flashcards.dao.DictionaryRepository;
import com.gitlab.sszuev.flashcards.domain.*;
import com.gitlab.sszuev.flashcards.dto.CardRecord;
import com.gitlab.sszuev.flashcards.dto.EntityMapper;
import com.gitlab.sszuev.flashcards.service.CardService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by @ssz on 08.05.2021.
 */
@Service
public class CardServiceImpl implements CardService {
    private final DictionaryRepository repository;
    private final EntityMapper mapper;
    private final int numberOfWordsInRun;

    public CardServiceImpl(@Value("${app.behaviour.words:10}") int numberOfWordsInRun,
                           DictionaryRepository repository, EntityMapper mapper) {
        if (numberOfWordsInRun <= 0)
            throw new IllegalArgumentException();
        this.numberOfWordsInRun = numberOfWordsInRun;
        this.repository = Objects.requireNonNull(repository);
        this.mapper = Objects.requireNonNull(mapper);
    }

    @Transactional(readOnly = true)
    @Override
    public List<String> getDictionaryNames() {
        return repository.streamAllByUserId(User.DEFAULT_USER_ID)
                .map(Dictionary::getName)
                .collect(Collectors.toUnmodifiableList());
    }

    @Transactional(readOnly = true)
    @Override
    public CardRecord getCard(String dictionaryName, Integer cardIndex) {
        int i = cardIndex == null ? 0 : cardIndex;
        Dictionary dic = getDictionary(dictionaryName);
        long count = dic.getCardsCount();
        if (count == 0 || i >= count) {
            return null;
        }
        return mapper.toRecord(dic.getCard(i), dic.getSourceLanguage());
    }

    @Transactional(readOnly = true)
    @Override
    public List<CardRecord> getCardDeck(String dictionary) {
        Dictionary dic = getDictionary(dictionary);
        Language lang = dic.getSourceLanguage();
        List<Card> toLearn = dic.cards().filter(x -> x.getStatus() != Status.LEARNED).collect(Collectors.toList());
        Collections.shuffle(toLearn, new Random());
        return toLearn.stream().limit(numberOfWordsInRun).map(c -> mapper.toRecord(c, lang))
                .collect(Collectors.toUnmodifiableList());
    }

    private Dictionary getDictionary(String name) {
        return repository.findByUserIdAndName(User.DEFAULT_USER_ID, name).orElseThrow(IllegalArgumentException::new);
    }
}
