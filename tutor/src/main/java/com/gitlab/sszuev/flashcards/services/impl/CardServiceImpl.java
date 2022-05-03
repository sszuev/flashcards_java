package com.gitlab.sszuev.flashcards.services.impl;

import com.gitlab.sszuev.flashcards.RunConfig;
import com.gitlab.sszuev.flashcards.domain.Card;
import com.gitlab.sszuev.flashcards.domain.Dictionary;
import com.gitlab.sszuev.flashcards.domain.Language;
import com.gitlab.sszuev.flashcards.domain.User;
import com.gitlab.sszuev.flashcards.dto.*;
import com.gitlab.sszuev.flashcards.parser.LingvoParser;
import com.gitlab.sszuev.flashcards.repositories.CardRepository;
import com.gitlab.sszuev.flashcards.repositories.DictionaryRepository;
import com.gitlab.sszuev.flashcards.services.CardService;
import com.gitlab.sszuev.flashcards.utils.CardUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.StringReader;
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
    private final LingvoParser lingvoParser;

    public CardServiceImpl(DictionaryRepository dictionaryRepository,
                           CardRepository cardRepository,
                           EntityMapper mapper,
                           RunConfig config,
                           LingvoParser lingvoParser) {
        this.dictionaryRepository = Objects.requireNonNull(dictionaryRepository);
        this.cardRepository = Objects.requireNonNull(cardRepository);
        this.mapper = Objects.requireNonNull(mapper);
        this.config = Objects.requireNonNull(config);
        this.lingvoParser = Objects.requireNonNull(lingvoParser);
    }

    @Transactional(readOnly = true)
    @Override
    public List<DictionaryResource> getDictionaries() {
        // todo: separated selects for total and learned counts
        return dictionaryRepository.streamAllByUserId(User.SYSTEM_USER.getID())
                .map(mapper::toResource).toList();
    }

    @Transactional()
    @Override
    public DictionaryResource uploadDictionary(String xml) {
        Dictionary dic = lingvoParser.parse(new StringReader(xml));
        LOGGER.debug("Dictionary '{}' is parsed.", dic.getName());
        dic = dictionaryRepository.save(dic);
        LOGGER.info("Dictionary '{}' is saved.", dic.getName());
        return mapper.toResource(dic);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CardResource> getAllCards(long dicId) {
        Language lang = getDictionary(dicId).getSourceLanguage();
        return cardRepository.streamByDictionaryId(dicId).map(c -> mapper.toResource(c, dicId, lang))
                .sorted(Comparator.comparing(CardResource::word)).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<CardResource> getNextCardDeck(long dicId) {
        return getNextCardDeck(dicId, config.getNumberOfWordsToShow(), true);
    }

    /**
     * {@inheritDoc}
     * <p>
     * It tries its best to return cards that do not overlap in their translations.
     */
    @Transactional(readOnly = true)
    @Override
    public List<CardResource> getNextCardDeck(long dicId, int length, boolean unknown) {
        Dictionary dic = getDictionary(dicId);
        Language lang = dic.getSourceLanguage();
        return getRandomCards(dicId, length, unknown)
                .stream().map(c -> mapper.toResource(c, dicId, lang)).toList();
    }

    public Dictionary getDictionary(long dicId) {
        return dictionaryRepository.findById(dicId)
                .orElseThrow(() -> new IllegalStateException("Can't find dictionary by id=" + dicId));
    }

    public Collection<Card> getRandomCards(long dicId, int length, boolean unknown) {
        List<Card> cards;
        if (unknown) {
            cards = cardRepository.streamByDictionaryIdAndAnsweredLessThan(dicId, config.getNumberOfRightAnswers())
                .collect(Collectors.toList());
        } else {
            cards = cardRepository.streamByDictionaryId(dicId).collect(Collectors.toList());
        }
        if (cards.size() < length * 1.2) {
            Collections.shuffle(cards, new Random());
            return cards;
        }
        if (cards.size() < length * 2) {
            length = cards.size() / 2;
        }
        return CardUtils.selectRandomNonSimilarCards(cards, length);
    }

    @Transactional
    @Override
    public void update(List<CardUpdateResource> data) {
        update(data.stream().collect(Collectors.toMap(CardUpdateResource::id, CardUpdateResource::details)));
    }

    @Transactional
    @Override
    public long save(CardResource resource) {
        LOGGER.info("Save {}", resource);
        Dictionary dic = dictionaryRepository.findById(resource.dictionaryId())
                .orElseThrow(() -> new IllegalArgumentException("Can't find dictionary = " + resource.dictionaryId()));
        Card card = mapper.fromResource(resource, dic);
        return cardRepository.save(card).getID();
    }

    @Transactional
    @Override
    public void deleteCard(long cardId) {
        LOGGER.info("Delete card with id={}", cardId);
        cardRepository.deleteById(cardId);
    }

    @Transactional
    @Override
    public void resetCardStatus(long cardId) {
        LOGGER.info("Reset the status for the card with id={}", cardId);
        cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Can't find card=" + cardId)).setAnswered(null);
    }

    private void update(Map<Long, Map<Stage, Integer>> data) {
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

            Map<Stage, Integer> map = data.get(id);
            Map<Stage, Long> details = mapper.readDetailsAsMap(card.getDetails());
            merge(details, map);
            card.setDetails(mapper.writeDetailsAsString(details));
            int answered = Optional.ofNullable(card.getAnswered()).orElse(0);
            int answeredUpdate = (int) map.values().stream().filter(x -> x > 0).count();
            if (answeredUpdate == map.size()) {
                answered += answeredUpdate;
            }
            String status = answered >= config.getNumberOfRightAnswers() ? "LEARNED" : "IN-PROGRESS";
            card.setAnswered(answered);
            LOGGER.info("Update card={} (status={}, answered={}), the data={}, text='{}'",
                    id, status, answered, map, card.getText());
            cardRepository.save(card);
        }
    }

    private static <X> void merge(Map<X, Long> map, Map<X, Integer> add) {
        // if no answer -> map contains 1 for the given stage
        // if true -> shift left and plus 1
        // if false -> shift left
        add.forEach((k, v) -> map.put(k, add(map.computeIfAbsent(k, i -> 1L), v)));
    }

    private static Long add(long val, int b) {
        val = val << 1;
        if (b > 0) {
            val += 1;
        }
        return val;
    }
}
