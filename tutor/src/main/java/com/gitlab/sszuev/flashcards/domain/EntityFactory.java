package com.gitlab.sszuev.flashcards.domain;

import java.util.Collection;
import java.util.List;

/**
 * Created by @ssz on 09.05.2021.
 */
public class EntityFactory {

    public static Dictionary newDictionary(long userId,
                                           String name,
                                           Language srcLanguage,
                                           Language dstLanguage,
                                           List<Card> cards) {
        Dictionary res = new Dictionary();
        res.setUser(new User());
        res.getUser().setID(userId);
        res.setName(name);
        res.setSourceLanguage(srcLanguage);
        res.setTargetLanguage(dstLanguage);
        res.setCards(cards);
        cards.forEach(c -> c.setDictionary(res));
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
        examples.forEach(x -> x.setCard(res));
        translations.forEach(x -> x.setCard(res));
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
