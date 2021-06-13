package com.gitlab.sszuev.flashcards.services.impl;

import com.gitlab.sszuev.flashcards.RunConfig;
import com.gitlab.sszuev.flashcards.domain.Dictionary;
import com.gitlab.sszuev.flashcards.domain.*;
import com.gitlab.sszuev.flashcards.dto.*;
import com.gitlab.sszuev.flashcards.repositories.CardRepository;
import com.gitlab.sszuev.flashcards.repositories.DictionaryRepository;
import com.gitlab.sszuev.flashcards.services.CardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by @ssz on 08.05.2021.
 */
@Service
public class CardServiceImpl implements CardService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CardServiceImpl.class);

    private final DictionaryRepository dictionaryRepository;
    private final CardRepository cardRepository;
    private final EntityMapper mapper;
    private final RunConfig config;

    public CardServiceImpl(DictionaryRepository dictionaryRepository,
                           CardRepository cardRepository,
                           EntityMapper mapper,
                           RunConfig config) {
        this.config = Objects.requireNonNull(config);
        this.dictionaryRepository = Objects.requireNonNull(dictionaryRepository);
        this.cardRepository = Objects.requireNonNull(cardRepository);
        this.mapper = Objects.requireNonNull(mapper);
    }

    @Transactional(readOnly = true)
    @Override
    public List<DictionaryResource> getDictionaries() {
        return dictionaryRepository.streamAllByUserId(User.DEFAULT_USER_ID)
                .map(mapper::createResource)
                .collect(Collectors.toUnmodifiableList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<CardResource> getCardDeck(long dicId) {
        Dictionary dic = dictionaryRepository.findById(dicId)
                .orElseThrow(() -> new IllegalStateException("Can't find dictionary by id=" + dicId));
        Language lang = dic.getSourceLanguage();
        List<Card> toLearn = cardRepository.streamByDictionaryIdAndStatusIn(dic.getID(),
                List.of(Status.UNKNOWN, Status.IN_PROCESS)).collect(Collectors.toList());
        Collections.shuffle(toLearn, new Random());
        return toLearn.stream().limit(config.getNumberOfWordsToShow()).map(c -> mapper.createResource(c, lang))
                .collect(Collectors.toUnmodifiableList());
    }

    @Transactional
    @Override
    public void update(List<CardRequest> data) {
        update(data.stream().collect(Collectors.toMap(CardRequest::getId, CardRequest::getDetails)));
    }

    private void update(Map<Long, Map<Stage, Boolean>> data) {
        Map<Long, Card> cards = cardRepository.streamByIdIn(data.keySet())
                .collect(Collectors.toMap(Card::getID, Function.identity()));
        if (cards.size() != data.size()) {
            Set<Long> ids = new HashSet<>(data.keySet());
            ids.removeAll(cards.keySet());
            throw new IllegalStateException("Can't find cards with ids=" + ids);
        }
        for (Long id : data.keySet()) {
            Card card = cards.get(id);
            if (card == null) {
                throw new IllegalStateException("Can't find card with id=" + id);
            }
            if (card.getStatus() == Status.LEARNED) {
                throw new IllegalStateException("Card id=" + id + " has already been learned.");
            }

            Map<Stage, Boolean> map = data.get(id);
            Map<Stage, List<Boolean>> details = mapper.readDetailsAsMap(card);
            addAll(details, map);
            card.setDetails(mapper.writeDetailsAsString(details));
            Status status;
            int answered = Optional.ofNullable(card.getAnswered()).orElse(0);
            int an = (int) map.values().stream().filter(x -> x).count();
            if (an == map.size() && (answered = answered + an) >= config.getNumberOfRightAnswers()) {
                status = Status.LEARNED;
            } else {
                status = Status.IN_PROCESS;
            }
            card.setStatus(status);
            card.setAnswered(answered);
            LOGGER.info("Update card={} (status={}, answered={}), the data={}, text='{}'",
                    id, status, answered, map, card.getText());
            cardRepository.save(card);
        }
    }

    private static <K, V> void addAll(Map<K, List<V>> base, Map<K, V> add) {
        add.forEach((k, v) -> base.computeIfAbsent(k, x -> new ArrayList<>()).add(v));
    }
}