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

    public String getSourceLang() {
        return sourceLang;
    }

    public String getTargetLang() {
        return targetLang;
    }

    public List<String> getPartsOfSpeech() {
        return partsOfSpeech;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getTotal() {
        return total;
    }

    public long getLearned() {
        return learned;
    }
}
