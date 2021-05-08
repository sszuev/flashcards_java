package com.gitlab.sszuev.flashcards.service;

import com.gitlab.sszuev.flashcards.dao.DictionaryRepository;
import com.gitlab.sszuev.flashcards.domain.Dictionary;
import com.gitlab.sszuev.flashcards.dto.CardRecord;
import com.gitlab.sszuev.flashcards.dto.EntityMapper;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Created by @ssz on 02.05.2021.
 */
@Service
public class CardsService {
    private final DictionaryRepository repository;
    private final EntityMapper mapper;

    public CardsService(DictionaryRepository repository, EntityMapper mapper) {
        this.repository = Objects.requireNonNull(repository);
        this.mapper = Objects.requireNonNull(mapper);
    }

    public CardRecord getCard(String dictionaryName, Integer cardIndex) {
        int i = cardIndex == null ? 0 : cardIndex;
        Dictionary dic = repository.findByName(dictionaryName).orElseThrow(IllegalArgumentException::new);
        long count = dic.getCardsCount();
        if (count == 0 || i >= count) {
            return null;
        }
        return mapper.toRecord(dic.getCard(i));
    }

}
