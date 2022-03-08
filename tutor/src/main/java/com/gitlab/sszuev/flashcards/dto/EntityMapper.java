package com.gitlab.sszuev.flashcards.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gitlab.sszuev.flashcards.domain.Card;
import com.gitlab.sszuev.flashcards.domain.Dictionary;
import com.gitlab.sszuev.flashcards.domain.Example;
import com.gitlab.sszuev.flashcards.domain.Language;
import com.gitlab.sszuev.flashcards.domain.Status;
import com.gitlab.sszuev.flashcards.domain.Translation;
import com.gitlab.sszuev.flashcards.services.SoundService;
import com.gitlab.sszuev.flashcards.utils.CardUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by @ssz on 02.05.2021.
 */
@Component
public class EntityMapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(EntityMapper.class);

    private static final Map<String, Locale> LOCALES = new ConcurrentHashMap<>();
    private static final TypeReference<Map<Stage, Long>> DB_DETAILS_TYPE_REFERENCE = new TypeReference<>() {
    };

    private final ObjectMapper mapper;
    private final SoundService speaker;

    public EntityMapper(SoundService service, ObjectMapper mapper) {
        this.speaker = Objects.requireNonNull(service);
        this.mapper = Objects.requireNonNull(mapper);
    }

    public Card fromResource(CardResource card, Dictionary dictionary) {
        Card res = new Card();
        res.setID(card.id());
        res.setText(card.word());
        res.setDictionary(dictionary);
        res.setTranscription(card.transcription());
        res.setStatus(card.status());
        res.setAnswered(card.answered());
        res.setExamples(card.examples().stream().map(x -> newExample(x, res)).collect(Collectors.toList()));
        res.setTranslations(card.translations().stream().map(x -> newTranslation(x, res)).collect(Collectors.toList()));
        res.setPartOfSpeech(card.partOfSpeech());
        res.setDetails(writeDetailsAsString(card.details()));
        return res;
    }

    public CardResource toResource(Card card, long dictionaryId, Language lang) {
        String word = card.getText();
        List<List<String>> translations = card.translations()
                .map(c -> CardUtils.getWords(c.getText())).toList();
        int answered = Optional.ofNullable(card.getAnswered()).orElse(0);
        List<String> examples = card.examples().map(e -> e.getText()).toList();
        String transcription = card.getTranscription();
        String partOfSpeech = parsePartsOfSpeech(card.getPartOfSpeech(), lang);
        Map<Stage, Long> details = readDetailsAsMap(card.getDetails());
        return new CardResource(card.getID(), dictionaryId, word, transcription,
                partOfSpeech, translations, examples, speaker.getResourceName(word, lang.getID()), card.getStatus(), answered, details);
    }

    public DictionaryResource toResource(Dictionary dictionary) {
        long total = dictionary.cards().count();
        long learned = dictionary.cards().filter(x -> Status.LEARNED == x.getStatus()).count();
        String src = dictionary.getSourceLanguage().getID();
        String dst = dictionary.getTargetLanguage().getID();
        List<String> partsOfSpeech = parsePartsOfSpeech(dictionary.getSourceLanguage());
        return new DictionaryResource(dictionary.getID(), dictionary.getName(), src, dst, partsOfSpeech, total, learned);
    }

    private static List<String> parsePartsOfSpeech(Language language) {
        String partsOfSpeech = language.getPartsOfSpeech();
        if (partsOfSpeech == null) {
            return List.of();
        }
        Locale locale = getLocal(language);
        return Arrays.stream(partsOfSpeech.split(",")).map(x -> normalize(x, locale)).toList();
    }

    private static String parsePartsOfSpeech(String partsOfSpeech, Language language) {
        if (partsOfSpeech == null) {
            return null;
        }
        Locale locale = getLocal(language);
        return normalize(partsOfSpeech, locale);
    }

    private static Example newExample(String text, Card card) {
        Example res = new Example();
        res.setText(text);
        res.setCard(card);
        return res;
    }

    private static Translation newTranslation(List<String> text, Card card) {
        Translation res = new Translation();
        res.setText(String.join(",", text));
        res.setCard(card);
        return res;
    }

    private static String normalize(String partsOfSpeech, Locale locale) {
        return partsOfSpeech.trim().toLowerCase(locale);
    }

    public Map<Stage, Long> readDetailsAsMap(String details) {
        if (details == null) {
            return new HashMap<>();
        }
        try {
            return mapper.readValue(details, DB_DETAILS_TYPE_REFERENCE);
        } catch (JsonProcessingException e) {
            LOGGER.debug("Can't parse details: '" + details + "': " + e.getMessage());
            return new HashMap<>();
        }
    }

    public String writeDetailsAsString(Map<Stage, Long> details) {
        try {
            return mapper.writeValueAsString(details);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Can't convert " + details + " to string", e);
        }
    }

    private static Locale getLocal(Language lang) {
        return getLocal(lang.getID().toLowerCase(Locale.ROOT));
    }

    private static Locale getLocal(String lang) {
        return LOCALES.computeIfAbsent(lang, Locale::new);
    }
}
