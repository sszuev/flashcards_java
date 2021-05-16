package com.gitlab.sszuev.flashcards;

import com.gitlab.sszuev.flashcards.domain.Card;
import com.gitlab.sszuev.flashcards.domain.Dictionary;
import com.gitlab.sszuev.flashcards.domain.EntityFactory;
import com.gitlab.sszuev.flashcards.domain.Language;
import org.mockito.Mockito;

import java.util.List;

/**
 * Created by @ssz on 02.05.2021.
 */
public class TestUtils {

    public static Dictionary mockDictionary(String name) {
        Dictionary res = Mockito.mock(Dictionary.class);
        Mockito.when(res.getName()).thenReturn(name);
        return res;
    }

    public static Dictionary mockDictionary(Long id, String name, Language lang) {
        Dictionary res = mockDictionary(name);
        Mockito.when(res.getSourceLanguage()).thenReturn(lang);
        Mockito.when(res.getID()).thenReturn(id);
        return res;
    }

    public static Card mockCard(Long id, String word) {
        Card res = Mockito.mock(Card.class);
        Mockito.when(res.getText()).thenReturn(word);
        Mockito.when(res.getID()).thenReturn(id);
        return res;
    }

    public static Card createCard(long id, String word, int answered, String details) {
        Card res = EntityFactory.newCard(word, null, null, List.of(), List.of(), null, answered, details);
        res.setID(id);
        return res;
    }

}
