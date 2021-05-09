package com.gitlab.sszuev.flashcards.service.impl;

import com.gitlab.sszuev.flashcards.dao.DictionaryRepository;
import com.gitlab.sszuev.flashcards.domain.Dictionary;
import com.gitlab.sszuev.flashcards.domain.User;
import com.gitlab.sszuev.flashcards.dto.CardRecord;
import com.gitlab.sszuev.flashcards.dto.EntityMapper;
import com.gitlab.sszuev.flashcards.service.CardService;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
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
        Dictionary dic = repository.findByUserIdAndName(User.DEFAULT.getId(), dictionaryName).orElseThrow(IllegalArgumentException::new);
        long count = dic.getCardsCount();
        if (count == 0 || i >= count) {
            return null;
        }
        return mapper.toRecord(dic.getCard(i));
    }

    @Override
    public Stream<String> dictionaries() {
        return repository.streamAllByUserId(User.DEFAULT.getId()).map(Dictionary::getName);
    }

    public static class XXXY {
        public static void main(String... xxx) {
            IntStream.of(183764, 171324, 172115, 185122, 173605, 178353, 180670, 176923, 170432).forEach(new IntConsumer() {
                @Override
                public void accept(int k) {
                    String v = Integer.toBinaryString(k);
                    System.out.println(k + " => " + v + " " + v.codePoints().filter(x -> '1' == x).count());
                }
            });
        }
    }
}
