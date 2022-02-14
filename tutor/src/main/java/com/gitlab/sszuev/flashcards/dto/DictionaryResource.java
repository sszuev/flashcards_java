package com.gitlab.sszuev.flashcards.dto;

/**
 * Created by @ssz on 16.05.2021.
 */
public record DictionaryResource(long id, String name, String sourceLang,
                                 String targetLang, long total, long learned) {

    public String getSourceLang() {
        return sourceLang;
    }

    public String getTargetLang() {
        return targetLang;
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
