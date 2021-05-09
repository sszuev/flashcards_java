package com.gitlab.sszuev.flashcards.domain;

import java.util.Collection;
import java.util.List;

/**
 * Created by @ssz on 09.05.2021.
 */
public class EntityFactory {

    public static Dictionary newDictionary(User user,
                                           String name,
                                           Language srcLanguage,
                                           Language dstLanguage,
                                           List<Card> cards) {
        Dictionary res = new Dictionary();
        res.setUser(user);
        res.setName(name);
        res.setSrcLanguage(srcLanguage);
        res.setDstLanguage(dstLanguage);
        res.setCards(cards);
        return res;
    }

    public static Card newCard(String word,
                               String transcription,
                               PartOfSpeech partOfSpeech,
                               Collection<Translation> translations,
                               Collection<Example> examples,
                               Status status,
                               String details) {
        Card res = new Card();
        res.setText(word);
        res.setTranscription(transcription);
        res.setPartOfSpeech(partOfSpeech);
        res.setExamples(examples);
        res.setStatus(status);
        res.setDetails(details);
        res.setTranslations(translations);
        return res;
    }

    public static Example newExample(String text) {
        Example res = new Example();
        res.setText(text);
        return res;
    }

    public static Translation newTranslation(String text) {
        Translation res = new Translation();
        res.setText(text);
        return res;
    }
}
