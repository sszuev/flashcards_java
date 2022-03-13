package com.gitlab.sszuev.flashcards.domain;

import java.util.Collection;
import java.util.List;

/**
 * A helper (factory) to produce entities.
 * <p>
 * Created by @ssz on 09.05.2021.
 */
public class EntityFactory {

    public static User newUser(Long id, String login, String pwd, int role) {
        User res = new User();
        res.setID(id);
        res.setLogin(login);
        res.setPassword(pwd);
        res.setRole(role);
        return res;
    }

    public static Language newLanguage(String id, String partsOfSpeech) {
        Language res = new Language();
        res.setID(id);
        res.setPartsOfSpeech(partsOfSpeech);
        return res;
    }

    public static Dictionary newDictionary(User user,
                                           String name,
                                           Language srcLanguage,
                                           Language dstLanguage,
                                           List<Card> cards) {
        Dictionary res = new Dictionary();
        res.setUser(user);
        res.setName(name);
        res.setSourceLanguage(srcLanguage);
        res.setTargetLanguage(dstLanguage);
        res.setCards(cards);
        cards.forEach(c -> c.setDictionary(res));
        return res;
    }

    public static Card newCard(String word,
                               String transcription,
                               String partOfSpeech,
                               Collection<Translation> translations,
                               Collection<Example> examples,
                               Integer answered,
                               String details) {
        Card res = new Card();
        res.setText(word);
        res.setTranscription(transcription);
        res.setPartOfSpeech(partOfSpeech);
        res.setExamples(examples);
        res.setAnswered(answered);
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
