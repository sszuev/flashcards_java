package com.gitlab.sszuev.flashcards.service;

import com.gitlab.sszuev.flashcards.TestUtils;
import com.gitlab.sszuev.flashcards.dao.DictionaryRepository;
import com.gitlab.sszuev.flashcards.domain.Card;
import com.gitlab.sszuev.flashcards.domain.Dictionary;
import com.gitlab.sszuev.flashcards.domain.Language;
import com.gitlab.sszuev.flashcards.domain.User;
import com.gitlab.sszuev.flashcards.dto.CardRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

/**
 * Created by @ssz on 02.05.2021.
 */
@SpringBootTest
@TestPropertySource(properties = {"app.behaviour.words=" + CardServiceTest.NUMBER_OF_WORDS_PER_RUN})
public class CardServiceTest {
    static final int NUMBER_OF_WORDS_PER_RUN = 5;

    @Autowired
    private CardService service;
    @MockBean
    private DictionaryRepository repository;
    @MockBean
    private SoundService soundService;

    @Test
    public void testGetCard() {
        Language lang = () -> "ue";
        String word = "TheWord";
        String dicName = "xxx";
        String sound = "yyy";

        int index = 42;
        Card card = TestUtils.mockCard(word);
        Dictionary dic = TestUtils.mockDictionary(dicName, lang);
        Mockito.when(dic.getCard(Mockito.eq(index))).thenReturn(card);
        Mockito.when(dic.getCardsCount()).thenReturn(4200L);
        Mockito.when(repository.findByUserIdAndName(Mockito.eq(User.DEFAULT_USER_ID), Mockito.eq(dicName)))
                .thenReturn(Optional.of(dic));
        Mockito.when(soundService.getResourceName(Mockito.eq(word), Mockito.eq(lang.name()))).thenReturn(sound);

        CardRecord res = service.getCard(dicName, index);
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
        Mockito.when(dic.cards()).thenReturn(words.stream().map(TestUtils::mockCard));

        Mockito.when(repository.findByUserIdAndName(Mockito.eq(User.DEFAULT_USER_ID), Mockito.eq(dicName)))
                .thenReturn(Optional.of(dic));

        List<CardRecord> res = service.getCardDeck(dicName);
        Assertions.assertNotNull(res);
        Assertions.assertEquals(NUMBER_OF_WORDS_PER_RUN, res.size());
        Assertions.assertEquals(NUMBER_OF_WORDS_PER_RUN, new HashSet<>(res).size());
        res.forEach(s -> Assertions.assertTrue(words.contains(s.getWord())));
    }

    @Test
    public void testListDictionaries() {
        List<String> given = List.of("A", "B");
        Mockito.when(repository.streamAllByUserId(Mockito.eq(User.DEFAULT_USER_ID)))
                .thenReturn(given.stream().map(TestUtils::mockDictionary));

        Assertions.assertEquals(given, service.getDictionaryNames());
    }

}
