package com.gitlab.sszuev.flashcards.service;

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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by @ssz on 02.05.2021.
 */
@SpringBootTest
public class CardServiceTest {
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
        Card card = Mockito.mock(Card.class);
        Mockito.when(card.getText()).thenReturn(word);
        Dictionary dic = mockDictionary(dicName, lang);
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
    public void testListDictionaries() {
        List<String> given = List.of("A", "B");
        Mockito.when(repository.streamAllByUserId(Mockito.eq(User.DEFAULT_USER_ID)))
                .thenReturn(given.stream().map(CardServiceTest::mockDictionary));

        Assertions.assertEquals(given, service.dictionaries().collect(Collectors.toList()));
    }

    public static Dictionary mockDictionary(String name) {
        Dictionary res = Mockito.mock(Dictionary.class);
        Mockito.when(res.getName()).thenReturn(name);
        return res;
    }

    public static Dictionary mockDictionary(String name, Language lang) {
        Dictionary res = mockDictionary(name);
        Mockito.when(res.getSourceLanguage()).thenReturn(lang);
        return res;
    }
}
