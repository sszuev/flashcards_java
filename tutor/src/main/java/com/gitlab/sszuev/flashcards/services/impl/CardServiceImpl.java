package com.gitlab.sszuev.flashcards.services.impl;

import com.gitlab.sszuev.flashcards.RunConfig;
import com.gitlab.sszuev.flashcards.domain.Card;
import com.gitlab.sszuev.flashcards.domain.Dictionary;
import com.gitlab.sszuev.flashcards.domain.Language;
import com.gitlab.sszuev.flashcards.domain.Status;
import com.gitlab.sszuev.flashcards.domain.User;
import com.gitlab.sszuev.flashcards.dto.CardRequest;
import com.gitlab.sszuev.flashcards.dto.CardResource;
import com.gitlab.sszuev.flashcards.dto.DictionaryResource;
import com.gitlab.sszuev.flashcards.dto.EntityMapper;
import com.gitlab.sszuev.flashcards.dto.Stage;
import com.gitlab.sszuev.flashcards.repositories.CardRepository;
import com.gitlab.sszuev.flashcards.repositories.DictionaryRepository;
import com.gitlab.sszuev.flashcards.services.CardService;
import com.gitlab.sszuev.flashcards.utils.CardUtils;
import com.gitlab.sszuev.flashcards.utils.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
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
        // todo: separated selects for total and learned counts
        return dictionaryRepository.streamAllByUserId(User.SYSTEM_USER.getID())
                .map(mapper::createResource).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<CardResource> getAllCards(long dicId) {
        Language lang = getDictionary(dicId).getSourceLanguage();
        return cardRepository.streamByDictionaryId(dicId).map(c -> mapper.createResource(c, lang)).toList();
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
                .stream().map(c -> mapper.createResource(c, lang)).toList();
    }

    public Dictionary getDictionary(long dicId) {
        return dictionaryRepository.findById(dicId)
                .orElseThrow(() -> new IllegalStateException("Can't find dictionary by id=" + dicId));
    }

    public Collection<Card> getRandomCards(long dicId, int length, boolean unknown) {
        List<Card> cards;
        if (unknown) {
            cards = cardRepository.streamByDictionaryIdAndStatusIn(dicId,
                    List.of(Status.UNKNOWN, Status.IN_PROCESS)).collect(Collectors.toList());
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
            CollectionUtils.addAll(details, map);
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

}
