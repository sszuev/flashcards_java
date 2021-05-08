package com.gitlab.sszuev.flashcards.rest;

import com.gitlab.sszuev.flashcards.dto.CardRecord;
import com.gitlab.sszuev.flashcards.service.CardService;
import com.gitlab.sszuev.flashcards.service.SoundService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by @ssz on 02.05.2021.
 */
@RestController
public class CardsController {
    private final CardService cardService;
    private final SoundService soundService;

    public CardsController(CardService cardService, SoundService soundService) {
        this.cardService = Objects.requireNonNull(cardService);
        this.soundService = Objects.requireNonNull(soundService);
    }

    @GetMapping("/api/dictionaries")
    public List<String> getDictionaries() {
        return cardService.dictionaries().collect(Collectors.toUnmodifiableList());
    }

    @GetMapping("/api/cards/{dictionary}/{index}")
    public CardRecord getCard(@PathVariable String dictionary, @PathVariable Integer index) {
        return cardService.getCard(dictionary, index);
    }

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
