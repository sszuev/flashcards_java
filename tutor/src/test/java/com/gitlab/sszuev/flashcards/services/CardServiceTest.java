package com.gitlab.sszuev.flashcards.services;

import com.gitlab.sszuev.flashcards.TestUtils;
import com.gitlab.sszuev.flashcards.domain.Card;
import com.gitlab.sszuev.flashcards.domain.Dictionary;
import com.gitlab.sszuev.flashcards.domain.Language;
import com.gitlab.sszuev.flashcards.domain.Status;
import com.gitlab.sszuev.flashcards.domain.User;
import com.gitlab.sszuev.flashcards.dto.CardRequest;
import com.gitlab.sszuev.flashcards.dto.CardResource;
import com.gitlab.sszuev.flashcards.dto.DictionaryResource;
import com.gitlab.sszuev.flashcards.dto.Stage;
import com.gitlab.sszuev.flashcards.repositories.CardRepository;
import com.gitlab.sszuev.flashcards.repositories.DictionaryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by @ssz on 02.05.2021.
 */
@SpringBootTest
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
        Language lang = () -> "ue";
        long dicId = 42;
        String dicName = "xxx";

        Map<Long, String> words = Map.of(-1L, "A", -2L, "B", -3L, "C", -4L, "D", -5L, "E", -6L,
                "F", -7L, "G", -8L, "H", -9L, "I", -10L, "K");

        Dictionary dic = TestUtils.mockDictionary(dicId, dicName, lang);
        Mockito.when(cardRepository.streamByDictionaryIdAndStatusIn(Mockito.eq(dicId),
                Mockito.eq(List.of(Status.UNKNOWN, Status.IN_PROCESS))))
                .thenReturn(words.entrySet().stream().map(e -> {
                    String word = e.getValue();
                    Mockito.when(soundService.getResourceName(Mockito.eq(word), Mockito.eq(lang.name())))
                            .thenReturn("sound-" + word);
                    return TestUtils.mockCard(e.getKey(), word);
                }));

        Mockito.when(dictionaryRepository.findById(Mockito.eq(dicId))).thenReturn(Optional.of(dic));

        List<CardResource> res = service.getNextCardDeck(dicId);
        Assertions.assertNotNull(res);
        Assertions.assertEquals(NUMBER_OF_WORDS_PER_RUN, res.size());
        Assertions.assertEquals(NUMBER_OF_WORDS_PER_RUN, new HashSet<>(res).size());
        res.forEach(s -> {
            String w = words.get(s.getId());
            Assertions.assertEquals(w, s.getWord());
            Assertions.assertEquals("sound-" + w, s.getSound());
        });
    }

    @Test
    public void testNextDeckWithAllFewItems() {
        Language lang = () -> "eq";
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
    public void testListDictionaries() {
        long id1 = -1;
        long id2 = -2;
        String name1 = "A";
        String name2 = "B";
        String lang1 = "ee";
        String lang2 = "rr";
        String lang3 = "xx";
        Map<Status, Integer> cards1 = Map.of(Status.IN_PROCESS, 2, Status.LEARNED, 3);
        Map<Status, Integer> cards2 = Map.of(Status.UNKNOWN, 1, Status.LEARNED, 42);

        Dictionary dic1 = TestUtils.mockDictionary(id1, name1, () -> lang1, () -> lang2, cards1);
        Dictionary dic2 = TestUtils.mockDictionary(id2, name2, () -> lang2, () -> lang3, cards2);

        Mockito.when(dictionaryRepository.streamAllByUserId(Mockito.eq(User.SYSTEM_USER.getID())))
                .thenReturn(Stream.of(dic1, dic2));

        List<DictionaryResource> res = service.getDictionaries();
        Assertions.assertNotNull(res);
        Assertions.assertEquals(2, res.size());
        assertDictionaryResource(res.get(0), id1, name1, lang1, lang2, cards1);
        assertDictionaryResource(res.get(1), id2, name2, lang2, lang3, cards2);
    }

    private void assertDictionaryResource(DictionaryResource res,
                                          long id, String name, String src, String dst, Map<Status, Integer> data) {
        Assertions.assertEquals(name, res.getName());
        Assertions.assertEquals(id, res.getId());
        Assertions.assertEquals(src, res.getSourceLang());
        Assertions.assertEquals(dst, res.getTargetLang());
        Assertions.assertEquals(data.get(Status.LEARNED).longValue(), res.getLearned());
        Assertions.assertEquals(data.values().stream().mapToLong(x -> x).sum(), res.getTotal());
    }

    @Test
    public void testUpdate() {
        long id1 = -1;
        long id2 = -2;
        List<CardRequest> requests = List.of(new CardRequest(id1, Map.of(Stage.SELF_TEST, true)),
                new CardRequest(id2, Map.of(Stage.OPTIONS, false)));
        Collection<Long> ids = requests.stream().map(CardRequest::getId).collect(Collectors.toSet());
        Card card1 = TestUtils.createCard(id1, "x", 2, "{\"writing\":[true,true],\"self-test\":[false]}");
        Card card2 = TestUtils.createCard(id2, "y", 1, "{\"mosaic\":[true],\"self-test\":[false]}");

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
        Assertions.assertEquals(Status.LEARNED, card1.getStatus());
        Assertions.assertEquals(Status.IN_PROCESS, card2.getStatus());

        Assertions.assertEquals(3, card1.getAnswered());
        Assertions.assertEquals(1, card2.getAnswered());
    }

    @Test
    public void testGetAllCards() {
        Language lang = () -> "xxx";
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
                res.stream().map(CardResource::getWord).collect(Collectors.toList()));
    }
}
