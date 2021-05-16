package com.gitlab.sszuev.flashcards;

import com.gitlab.sszuev.flashcards.domain.*;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by @ssz on 02.05.2021.
 */
public class TestUtils {

    public static Dictionary mockDictionary(Long id, String name) {
        Dictionary res = Mockito.mock(Dictionary.class);
        Mockito.when(res.getID()).thenReturn(id);
        Mockito.when(res.getName()).thenReturn(name);
        return res;
    }

    public static Dictionary mockDictionary(Long id, String name, Language lang) {
        Dictionary res = mockDictionary(id, name);
        Mockito.when(res.getSourceLanguage()).thenReturn(lang);
        return res;
    }

    public static Dictionary mockDictionary(Long id, String name, Language src, Language dst) {
        Dictionary res = mockDictionary(id, name, src);
        Mockito.when(res.getTargetLanguage()).thenReturn(dst);
        return res;
    }

    public static Dictionary mockDictionary(Long id, String name, Language src, Language dst, Map<Status, Integer> cards) {
        Dictionary res = mockDictionary(id, name, src, dst);
        List<Card> mockCards = cards.entrySet().stream()
                .flatMap(e -> IntStream.range(0, e.getValue()).mapToObj(x -> mockCard(e.getKey())))
                .collect(Collectors.toList());
        Mockito.when(res.cards()).thenAnswer(i -> mockCards.stream());
        return res;
    }

    public static Card mockCard() {
        return Mockito.mock(Card.class);
    }

    public static Card mockCard(Long id, String word) {
        Card res = mockCard();
        Mockito.when(res.getText()).thenReturn(word);
        Mockito.when(res.getID()).thenReturn(id);
        return res;
    }

    public static Card mockCard(Status status) {
        Card res = mockCard();
        Mockito.when(res.getStatus()).thenReturn(status);
        return res;
    }

    public static Card createCard(long id, String word, int answered, String details) {
        Card res = EntityFactory.newCard(word, null, null, List.of(), List.of(), null, answered, details);
        res.setID(id);
        return res;
    }

}
