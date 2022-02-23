package com.gitlab.sszuev.flashcards.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Describes the language.
 * <p>
 * Created by @ssz on 29.04.2021.
 */
@Entity
@Table(name = "languages")
public class Language {

    @Id
    private String id;
    @Column(name = "parts_of_speech")
    private String partsOfSpeech;

    public String getID() {
        return id;
    }

    public void setID(String id) {
        this.id = id;
    }

    public String getPartsOfSpeech() {
        return partsOfSpeech;
    }

    public void setPartsOfSpeech(String partsOfSpeech) {
        this.partsOfSpeech = partsOfSpeech;
    }

}
