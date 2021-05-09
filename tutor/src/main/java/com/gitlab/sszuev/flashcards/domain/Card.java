package com.gitlab.sszuev.flashcards.domain;

import javax.persistence.*;
import java.util.Collection;
import java.util.stream.Stream;

/**
 * Describes a card entity.
 * <p>
 * Created by @ssz on 01.05.2021.
 */
@Entity
@Table(name = "cards")
public class Card extends WithText implements HasID {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToMany(targetEntity = Translation.class
            , mappedBy = "card"
            , orphanRemoval = true
            , cascade = CascadeType.ALL)
    private Collection<Translation> translations;
    @OneToMany(targetEntity = Example.class
            , mappedBy = "card"
            , orphanRemoval = true
            , cascade = CascadeType.ALL)
    private Collection<Example> examples;
    @Column
    private String transcription;
    @Column
    @Enumerated(EnumType.STRING)
    private Status status;
    @Column
    private String details;
    @Column(name = "part_of_speech")
    private String partOfSpeech;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dictionary_id", nullable = false)
    private Dictionary dictionary;

    @Override
    public Long getID() {
        return id;
    }

    @Override
    public void setID(Long id) {
        this.id = id;
    }

    public void setTranslations(Collection<Translation> translations) {
        this.translations = translations;
    }

    public void setExamples(Collection<Example> examples) {
        this.examples = examples;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getTranscription() {
        return transcription;
    }

    public void setTranscription(String transcription) {
        this.transcription = transcription;
    }

    public PartOfSpeech getPartOfSpeech() {
        return PartOfSpeech.fromString(partOfSpeech);
    }

    public void setPartOfSpeech(PartOfSpeech partOfSpeech) {
        this.partOfSpeech = PartOfSpeech.toString(partOfSpeech);
    }

    public Dictionary getDictionary() {
        return dictionary;
    }

    public void setDictionary(Dictionary dictionary) {
        this.dictionary = dictionary;
    }

    public Stream<Translation> translations() {
        return translations.stream();
    }

    public Stream<Example> examples() {
        return examples.stream();
    }
}
