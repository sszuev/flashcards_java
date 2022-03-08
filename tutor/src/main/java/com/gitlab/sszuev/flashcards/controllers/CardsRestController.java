package com.gitlab.sszuev.flashcards.controllers;

import com.gitlab.sszuev.flashcards.dto.CardResource;
import com.gitlab.sszuev.flashcards.dto.CardUpdateResource;
import com.gitlab.sszuev.flashcards.dto.DictionaryResource;
import com.gitlab.sszuev.flashcards.services.CardService;
import com.gitlab.sszuev.flashcards.services.SoundService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
    @GetMapping(value = "/api/dictionaries", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<DictionaryResource> getDictionaries() {
        return cardService.getDictionaries();
    }

    /**
     * Gets a list of card-resources by dictionary id.
     *
     * @return a {@link List} of {@link CardResource}s
     */
    @GetMapping(value = "/api/dictionaries/{dictionary}/cards", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CardResource> getDictionaryCards(@PathVariable(name = "dictionary") long dictionary) {
        return cardService.getAllCards(dictionary);
    }

    /**
     * Randomly peeks card-resources from the underlying db by the given dictionary id.
     * Non-idempotent: every time new result.
     *
     * @param dictionary {@code long} - id of dictionary
     * @param length     {@code Integer} - the desired size of result collections
     * @param unknown    {@code Boolean} - {@code true} to return only unknown words,
     *                   {@code false} or {@code null} if it is does not matter
     * @return a {@code List} of {@link CardResource}s
     */
    @GetMapping(value = "/api/dictionaries/{dictionary}/cards/random", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CardResource> getNextCardDeck(@PathVariable(name = "dictionary") long dictionary,
                                              @RequestParam(name = "length", required = false) Integer length,
                                              @RequestParam(name = "unknown", required = false) Boolean unknown) {
        if (length != null && unknown != null) {
            return cardService.getNextCardDeck(dictionary, length, unknown);
        }
        if (length == null && unknown == null) {
            return cardService.getNextCardDeck(dictionary);
        }
        throw new IllegalArgumentException();
    }

    @PostMapping(value = "/api/cards", consumes = MediaType.APPLICATION_JSON_VALUE)
    public long create(@RequestBody CardResource resource) {
        if (resource.id() != null) {
            throw new IllegalArgumentException("Existing resource specified: " + resource.id() + ".");
        }
        return cardService.save(resource);
    }

    @PutMapping(value = "/api/cards", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void update(@RequestBody CardResource resource) {
        if (resource.id() == null) {
            throw new IllegalArgumentException("The given resource has no id.");
        }
        cardService.save(resource);
    }

    @DeleteMapping(value = "/api/cards/{card}")
    public void delete(@PathVariable(name = "card") long cardId) {
        cardService.deleteCard(cardId);
    }

    /**
     * Updates cards in underlying db (while learning process).
     *
     * @param request a {@code List} of {@link CardUpdateResource}s
     */
    @PatchMapping(value = "/api/cards", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void update(@RequestBody List<CardUpdateResource> request) {
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
