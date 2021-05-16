package com.gitlab.sszuev.flashcards.service;

import com.gitlab.sszuev.flashcards.TestUtils;
import com.gitlab.sszuev.flashcards.dao.CardRepository;
import com.gitlab.sszuev.flashcards.dao.DictionaryRepository;
import com.gitlab.sszuev.flashcards.domain.Dictionary;
import com.gitlab.sszuev.flashcards.domain.*;
import com.gitlab.sszuev.flashcards.dto.CardRequest;
import com.gitlab.sszuev.flashcards.dto.CardResource;
import com.gitlab.sszuev.flashcards.dto.Stage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by @ssz on 02.05.2021.
 */
@SpringBootTest
@TestPropertySource(properties = {"app.behaviour.words=" + CardServiceTest.NUMBER_OF_WORDS_PER_RUN
        , "app.behaviour.answers=" + CardServiceTest.NUMBER_OF_ANSWERS_TO_LEARN})
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
    public void testGetCard() {
        Language lang = () -> "ue";
        String word = "TheWord";
        String dicName = "xxx";
        String sound = "yyy";

        int index = 42;
        Card card = TestUtils.mockCard(42L, word);
        Dictionary dic = TestUtils.mockDictionary(dicName, lang);
        Mockito.when(dic.getCard(Mockito.eq(index))).thenReturn(card);
        Mockito.when(dic.getCardsCount()).thenReturn(4200L);
        Mockito.when(dictionaryRepository.findByUserIdAndName(Mockito.eq(User.DEFAULT_USER_ID), Mockito.eq(dicName)))
                .thenReturn(Optional.of(dic));
        Mockito.when(soundService.getResourceName(Mockito.eq(word), Mockito.eq(lang.name()))).thenReturn(sound);

        CardResource res = service.getCard(dicName, index);
        Assertions.assertNotNull(res);
        Assertions.assertEquals(word, res.getWord());
        Assertions.assertEquals(sound, res.getSound());
    }

    @Test
    public void testGetCardDeck() {
        Language lang = () -> "ue";
        String dicName = "xxx";
        List<String> words = List.of("A", "B", "C", "D", "E", "W");

        Dictionary dic = TestUtils.mockDictionary(dicName, lang);
        Mockito.when(dic.cards()).thenReturn(words.stream().map(word -> TestUtils.mockCard(-1L, word)));

        Mockito.when(dictionaryRepository.findByUserIdAndName(Mockito.eq(User.DEFAULT_USER_ID), Mockito.eq(dicName)))
                .thenReturn(Optional.of(dic));

        List<CardResource> res = service.getCardDeck(dicName);
        Assertions.assertNotNull(res);
        Assertions.assertEquals(NUMBER_OF_WORDS_PER_RUN, res.size());
        Assertions.assertEquals(NUMBER_OF_WORDS_PER_RUN, new HashSet<>(res).size());
        res.forEach(s -> Assertions.assertTrue(words.contains(s.getWord())));
    }

    @Test
    public void testListDictionaries() {
        List<String> given = List.of("A", "B");
        Mockito.when(dictionaryRepository.streamAllByUserId(Mockito.eq(User.DEFAULT_USER_ID)))
                .thenReturn(given.stream().map(TestUtils::mockDictionary));

        Assertions.assertEquals(given, service.getDictionaryNames());
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

        Mockito.when(cardRepository.streamAllByIdIn(Mockito.eq(ids))).thenReturn(Stream.of(card1, card2));
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

}
