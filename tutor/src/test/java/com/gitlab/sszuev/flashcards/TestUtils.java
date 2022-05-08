package com.gitlab.sszuev.flashcards;

import com.gitlab.sszuev.flashcards.documents.Status;
import com.gitlab.sszuev.flashcards.domain.Card;
import com.gitlab.sszuev.flashcards.domain.Dictionary;
import com.gitlab.sszuev.flashcards.domain.EntityFactory;
import com.gitlab.sszuev.flashcards.domain.Language;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * Created by @ssz on 02.05.2021.
 */
public class TestUtils {
    private static final Random RANDOM = new Random();

    public static Language mockLanguage(String lang, List<String> partsOfSpeech) {
        Language res = Mockito.mock(Language.class);
        Mockito.when(res.getID()).thenReturn(lang);
        if (partsOfSpeech != null) {
            Mockito.when(res.getPartsOfSpeech()).thenReturn(String.join(",", partsOfSpeech));
        }
        return res;
    }

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

    public static Dictionary mockDictionary(Long id, String name,
                                            Language src, Language dst,
                                            int numberOfAnswersToLearn,
                                            Map<Status, Integer> cards) {
        Dictionary res = mockDictionary(id, name, src, dst);
        List<Card> mockCards = cards.entrySet().stream()
                .flatMap(e -> IntStream.range(0, e.getValue())
                        .mapToObj(x -> mockCard(numberOfAnswersToLearn, e.getKey()))).toList();
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

    public static Card mockCard(int numberOfAnswersToLearn, Status status) {
        Card res = mockCard();
        Integer answered = null;
        if (status == Status.IN_PROCESS) {
            answered = RANDOM.nextInt(numberOfAnswersToLearn);
        } else if (status == Status.LEARNED) {
            answered = numberOfAnswersToLearn;
        }
        Mockito.when(res.getAnswered()).thenReturn(answered);
        return res;
    }

    public static Card createCard(long id, String word, int answered, String details) {
        Card res = EntityFactory.newCard(word, null, null, List.of(), List.of(), answered, details);
        res.setID(id);
        return res;
    }

}
