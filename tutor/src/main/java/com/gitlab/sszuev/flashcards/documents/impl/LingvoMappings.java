package com.gitlab.sszuev.flashcards.documents.impl;

import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

@Component
public class LingvoMappings {
    public static final Map<String, StandardLanguage> LANGUAGE_MAP = Map.of(
            "1033", StandardLanguage.EN,
            "1049", StandardLanguage.RU);
    public static final Map<String, StandardPartOfSpeech> PART_OF_SPEECH_MAP = Map.of(
            "1", StandardPartOfSpeech.NOUN,
            "2", StandardPartOfSpeech.ADJECTIVE,
            "3", StandardPartOfSpeech.VERB);
    public static final Map<String, Status> STATUS_MAP = Map.of(
            "2", Status.UNKNOWN,
            "3", Status.IN_PROCESS,
            "4", Status.LEARNED);

    public String toLanguageTag(String id) {
        return byKey(LANGUAGE_MAP, id).name();
    }

    public String fromLanguageTag(String tag) {
        return byValue(LANGUAGE_MAP, tag);
    }

    public String toPartOfSpeechTag(String id) {
        return byKey(PART_OF_SPEECH_MAP, id).name();
    }

    public String fromPartOfSpeechTag(String tag) {
        return byValue(PART_OF_SPEECH_MAP, tag);
    }

    public Status toStatus(String id) {
        return byKey(STATUS_MAP, id);
    }

    public String fromStatus(Status status) {
        return byValue(STATUS_MAP, status.name());
    }

    /**
     * Impl notes:
     * Uses {@link StandardCharsets#UTF_16},
     * Lingvo ABBYY Tutor Words 16.1.3.70 requires UTF-16.
     *
     * @return {@link Charset}
     */
    public Charset charset() {
        return StandardCharsets.UTF_16;
    }

    private static <X extends Enum<X>> String byValue(Map<String, X> map, String value) {
        String v = Objects.requireNonNull(value);
        return map.entrySet().stream().filter(x -> v.equalsIgnoreCase(x.getValue().name()))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Can't find value '" + value + "'")).getKey();
    }

    private static <X extends Enum<X>> X byKey(Map<String, X> map, String key) {
        return Objects.requireNonNull(map.get(key), "Can't find key '" + key + "'");
    }
}
