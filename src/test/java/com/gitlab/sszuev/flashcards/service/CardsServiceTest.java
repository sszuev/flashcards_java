package com.gitlab.sszuev.flashcards.service;

import com.gitlab.sszuev.flashcards.dao.DictionaryRepository;
import com.gitlab.sszuev.flashcards.domain.Card;
import com.gitlab.sszuev.flashcards.domain.Dictionary;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

/**
 * Created by @ssz on 02.05.2021.
 */
@SpringBootTest
public class CardsServiceTest {
    @Autowired
    private CardsService service;
    @MockBean
    private DictionaryRepository repository;

    @Test
    public void testGetCard() {
        String wordName = "TheWord";
        String dicName = "xxx";
        int index = 42;
        Card card = Mockito.mock(Card.class);
        Mockito.when(card.getText()).thenReturn(wordName);
        Dictionary dic = Mockito.mock(Dictionary.class);
        Mockito.when(dic.getCard(Mockito.eq(index))).thenReturn(card);
        Mockito.when(dic.getCardsCount()).thenReturn(4200L);
        Mockito.when(repository.findByName(Mockito.eq(dicName))).thenReturn(Optional.of(dic));

        Assertions.assertEquals(wordName, service.getCard(dicName, index).getWord());
    }
}
