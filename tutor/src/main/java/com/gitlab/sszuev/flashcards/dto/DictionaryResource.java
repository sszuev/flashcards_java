package com.gitlab.sszuev.flashcards.dto;

import java.util.List;

/**
 * Created by @ssz on 16.05.2021.
 */
public record DictionaryResource(long id,
                                 String name,
                                 String sourceLang,
                                 String targetLang,
                                 List<String> partsOfSpeech,
                                 long total,
                                 long learned) {
}
