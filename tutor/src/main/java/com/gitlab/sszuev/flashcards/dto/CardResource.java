package com.gitlab.sszuev.flashcards.dto;

import com.gitlab.sszuev.flashcards.domain.Status;

import java.util.List;
import java.util.Map;

/**
 * Created by @ssz on 02.05.2021.
 */
@SuppressWarnings("unused")
public record CardResource(Long id,
                           long dictionaryId,
                           String word,
                           String transcription,
                           String partOfSpeech,
                           List<List<String>> translations,
                           List<String> examples,
                           String sound,
                           Status status,
                           Integer answered,
                           Map<Stage, Long> details) {
}
