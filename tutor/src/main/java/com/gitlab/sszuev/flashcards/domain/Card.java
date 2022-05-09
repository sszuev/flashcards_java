package com.gitlab.sszuev.flashcards.domain;

import javax.persistence.*;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
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

    @org.hibernate.annotations.Fetch(org.hibernate.annotations.FetchMode.SUBSELECT)
    @OneToMany(targetEntity = Translation.class
            , mappedBy = "card"
            , orphanRemoval = true
            , cascade = CascadeType.ALL)
    private Set<Translation> translations;

    @org.hibernate.annotations.Fetch(org.hibernate.annotations.FetchMode.SUBSELECT)
    @OneToMany(targetEntity = Example.class
            , mappedBy = "card"
            , orphanRemoval = true
            , cascade = CascadeType.ALL)
    private Set<Example> examples;

    @Column
    private String transcription;

    @Column(name = "part_of_speech")
    private String partOfSpeech;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dictionary_id", nullable = false)
    private Dictionary dictionary;

    @Column
    private String details;

    @Column
    private Integer answered;

    @Override
    public Long getID() {
        return id;
    }

    @Override
    public void setID(Long id) {
        this.id = id;
    }

    public void setTranslations(Collection<Translation> translations) {
        this.translations = asSet(translations);
    }

    public void setExamples(Collection<Example> examples) {
        this.examples = asSet(examples);
    }

    private <X> Set<X> asSet(Collection<X> collection) {
        return collection instanceof Set ? (Set<X>) collection : new LinkedHashSet<>(collection);
    }

    public String getTranscription() {
        return transcription;
    }

    public void setTranscription(String transcription) {
        this.transcription = transcription;
    }

    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(String partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
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

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Integer getAnswered() {
        return answered;
    }

    public void setAnswered(Integer answered) {
        this.answered = answered;
    }

}
