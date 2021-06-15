package com.gitlab.sszuev.flashcards.controllers;

import com.gitlab.sszuev.flashcards.dto.CardRequest;
import com.gitlab.sszuev.flashcards.dto.CardResource;
import com.gitlab.sszuev.flashcards.dto.DictionaryResource;
import com.gitlab.sszuev.flashcards.services.CardService;
import com.gitlab.sszuev.flashcards.services.SoundService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * Created by @ssz on 02.05.2021.
 */
@RestController
public class CardsRestController {
    private final CardService cardService;
    private final SoundService soundService;

    public CardsRestController(CardService cardService, SoundService soundService) {
        this.cardService = Objects.requireNonNull(cardService);
        this.soundService = Objects.requireNonNull(soundService);
    }

    /**
     * Gets a list of dictionary-resources.
     *
     * @return a {@link List} of {@link DictionaryResource}s
     */
    @GetMapping("/api/dictionaries")
    public List<DictionaryResource> getDictionaries() {
        return cardService.getDictionaries();
    }

    /**
     * Randomly peeks card-resources from the underlying db by the given dictionary id.
     * Non-idempotent: every time new result.
     *
     * @param dictionary {@code long} - id of dictionary
     * @return a {@code List} of {@link CardResource}s
     */
    @GetMapping("/api/cards/{dictionary}")
    public List<CardResource> getNextCardDeck(@PathVariable long dictionary) {
        return cardService.getNextCardDeck(dictionary);
    }

    /**
     * Updates cards in underlying db.
     *
     * @param request a {@code List} of {@link CardRequest}s
     */
    @PatchMapping(value = "/api/cards", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void update(@RequestBody List<CardRequest> request) {
        cardService.update(request);
    }

    /**
     * Gets an audio-resource by its id (as a path).
     *
     * @param path {@code String}
     * @return a {@link ResponseEntity} with {@link Resource}
     */
    @GetMapping("/api/sounds/{path}")
    public ResponseEntity<Resource> getSound(@PathVariable String path) {
        if (path == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Resource res = soundService.getResource(path);
        if (!res.exists()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        long length;
        try {
            length = res.contentLength();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("", UriComponentsBuilder.newInstance()
                .path(path).encode(StandardCharsets.UTF_8).toUriString());
        headers.setContentLength(length);
        headers.setContentType(new MediaType("audio", "wav"));
        return new ResponseEntity<>(res, headers, HttpStatus.OK);
    }
}
