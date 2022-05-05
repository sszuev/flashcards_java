package com.gitlab.sszuev.flashcards.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitlab.sszuev.flashcards.RunConfig;
import com.gitlab.sszuev.flashcards.TestUtils;
import com.gitlab.sszuev.flashcards.domain.Card;
import com.gitlab.sszuev.flashcards.domain.Dictionary;
import com.gitlab.sszuev.flashcards.domain.Language;
import com.gitlab.sszuev.flashcards.dto.CardResource;
import com.gitlab.sszuev.flashcards.dto.CardUpdateResource;
import com.gitlab.sszuev.flashcards.dto.EntityMapper;
import com.gitlab.sszuev.flashcards.dto.Stage;
import com.gitlab.sszuev.flashcards.repositories.CardRepository;
import com.gitlab.sszuev.flashcards.repositories.DictionaryRepository;
import com.gitlab.sszuev.flashcards.services.impl.CardServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by @ssz on 02.05.2021.
 */
@SpringBootTest
@ContextConfiguration(classes = {CardServiceImpl.class, RunConfig.class, EntityMapper.class, ObjectMapper.class})
@TestPropertySource(properties = {
        "app.loader.enabled=false",
        "app.tutor.run.words-for-show=" + CardServiceTest.NUMBER_OF_WORDS_PER_RUN,
        "app.tutor.run.answers=" + CardServiceTest.NUMBER_OF_ANSWERS_TO_LEARN
})
public class CardServiceTest {
    static final int NUMBER_OF_WORDS_PER_RUN = 5;
    static final int NUMBER_OF_ANSWERS_TO_LEARN = 3;

    @Autowired
    private CardService service;
    @MockBean
    private DictionaryRepository dictionaryRepository;
    @MockBean
    private CardRepository cardRepository;
    @MockBean
    private SoundService soundService;

    @Test
    public void testNextDeckWithUnknownManyItems() {
        String lang = "ue";
        long dicId = 42;
        String dicName = "xxx";

        Map<Long, String> words = Map.of(-1L, "A", -2L, "B", -3L, "C", -4L, "D", -5L, "E", -6L,
                "F", -7L, "G", -8L, "H", -9L, "I", -10L, "K");

        Dictionary dic = TestUtils.mockDictionary(dicId, dicName, TestUtils.mockLanguage(lang, null));
        Mockito.when(cardRepository.streamByDictionaryIdAndAnsweredLessThan(Mockito.eq(dicId),
                        Mockito.eq(NUMBER_OF_ANSWERS_TO_LEARN)))
                .thenReturn(words.entrySet().stream().map(e -> {
                    String word = e.getValue();
                    Mockito.when(soundService.getResourceName(Mockito.eq(word), Mockito.eq(lang)))
                            .thenReturn("sound-" + word);
                    return TestUtils.mockCard(e.getKey(), word);
                }));

        Mockito.when(dictionaryRepository.findById(Mockito.eq(dicId))).thenReturn(Optional.of(dic));

        List<CardResource> res = service.getNextCardDeck(dicId);
        Assertions.assertNotNull(res);
        Assertions.assertEquals(NUMBER_OF_WORDS_PER_RUN, res.size());
        Assertions.assertEquals(NUMBER_OF_WORDS_PER_RUN, new HashSet<>(res).size());
        res.forEach(s -> {
            String w = words.get(s.id());
            Assertions.assertEquals(w, s.word());
            Assertions.assertEquals("sound-" + w, s.sound());
        });
    }

    @Test
    public void testNextDeckWithAllFewItems() {
        Language lang = TestUtils.mockLanguage("eq", null);
        long dicId = 42;
        String dicName = "yyy";

        Map<Long, String> words = Map.of(-11L, "a", -12L, "b", -15L, "c");

        Dictionary dic = TestUtils.mockDictionary(dicId, dicName, lang);
        Mockito.when(cardRepository.streamByDictionaryId(Mockito.eq(dicId)))
                .thenReturn(words.entrySet().stream().map(e -> TestUtils.mockCard(e.getKey(), e.getValue())));

        Mockito.when(dictionaryRepository.findById(Mockito.eq(dicId))).thenReturn(Optional.of(dic));

        List<CardResource> res = service.getNextCardDeck(dicId, 15, false);
        Assertions.assertNotNull(res);
        Assertions.assertEquals(words.size(), res.size());
        Assertions.assertEquals(words.size(), new HashSet<>(res).size());
    }

    @Test
    public void testUpdate() {
        long id1 = -1;
        long id2 = -2;
        List<CardUpdateResource> requests = List.of(new CardUpdateResource(id1, Map.of(Stage.SELF_TEST, 1)),
                new CardUpdateResource(id2, Map.of(Stage.OPTIONS, 0)));
        Collection<Long> ids = requests.stream().map(CardUpdateResource::id).collect(Collectors.toSet());
        Card card1 = TestUtils.createCard(id1, "x", 2, "{\"writing\":3,\"self-test\":4}");
        Card card2 = TestUtils.createCard(id2, "y", 1, "{\"mosaic\":1,\"self-test\":0}");

        Mockito.when(cardRepository.streamByIdIn(Mockito.eq(ids))).thenReturn(Stream.of(card1, card2));
        List<Card> res = new ArrayList<>();
        Mockito.when(cardRepository.save(Mockito.any())).thenAnswer(i -> {
            Card card = i.getArgument(0);
            res.add(card);
            return card;
        });

        service.update(requests);
        Assertions.assertEquals(2, res.size());
        Assertions.assertSame(card1, res.get(0));
        Assertions.assertSame(card2, res.get(1));

        Assertions.assertEquals(id1, card1.getID());
        Assertions.assertEquals(id2, card2.getID());
        Assertions.assertEquals(3, card1.getAnswered());
        Assertions.assertEquals(1, card2.getAnswered());

        Assertions.assertEquals(3, card1.getAnswered());
        Assertions.assertEquals(1, card2.getAnswered());
    }

    @Test
    public void testGetAllCards() {
        Language lang = TestUtils.mockLanguage("xxx", null);
        long dicId = 42;
        String dicName = "xxx";
        List<Map.Entry<Long, String>> words = List.of(Map.entry(-1L, "C"), Map.entry(-2L, "A"),
                Map.entry(-4L, "G"), Map.entry(-5L, "f"), Map.entry(-42L, "a"));

        Dictionary dic = TestUtils.mockDictionary(dicId, dicName, lang);
        Mockito.when(cardRepository.streamByDictionaryId(Mockito.eq(dicId)))
                .thenReturn(words.stream()
                        .map(e -> TestUtils.mockCard(e.getKey(), e.getValue())));
        Mockito.when(dictionaryRepository.findById(Mockito.eq(dicId))).thenReturn(Optional.of(dic));

        List<CardResource> res = service.getAllCards(dicId);
        Assertions.assertNotNull(res);
        Assertions.assertEquals(words.size(), res.size());
        Assertions.assertEquals(words.stream().map(Map.Entry::getValue).sorted().toList(),
                res.stream().map(CardResource::word).collect(Collectors.toList()));
    }

    @Test
    public void testResetStatus() {
        Card card = TestUtils.createCard(42L, "x", 42, null);
        Assumptions.assumeTrue(42 == card.getAnswered());
        Mockito.when(cardRepository.findById(Mockito.eq(42L))).thenReturn(Optional.of(card));
        service.resetCardStatus(42L);
        Assertions.assertNull(card.getAnswered());
        Mockito.verify(cardRepository, Mockito.times(1)).findById(Mockito.eq(42L));
    }
}
