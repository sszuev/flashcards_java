package com.gitlab.sszuev.flashcards.dto;

/**
 * Created by @ssz on 16.05.2021.
 */
public final class DictionaryResource {
    private final long id;
    private final String name;
    private final String sourceLang;
    private final String targetLang;
    private final long total;
    private final long learned;

    public DictionaryResource(long id, String name, String sourceLang, String targetLang, long total, long learned) {
        this.id = id;
        this.name = name;
        this.sourceLang = sourceLang;
        this.targetLang = targetLang;
        this.total = total;
        this.learned = learned;
    }

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
