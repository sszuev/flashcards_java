package com.gitlab.sszuev.flashcards.rest;

import com.gitlab.sszuev.flashcards.dto.CardRecord;
import com.gitlab.sszuev.flashcards.service.CardsService;
import org.springframework.core.io.ClassPathResource;
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
import java.util.Objects;

/**
 * Created by @ssz on 02.05.2021.
 */
@RestController
public class CardsController {
    private final CardsService service;

    public CardsController(CardsService service) {
        this.service = Objects.requireNonNull(service);
    }

    @GetMapping("/api/card/{dictionary}/{index}")
    public CardRecord getCard(@PathVariable String dictionary, @PathVariable Integer index) {
        return service.getCard(dictionary, index);
    }

    @GetMapping("/api/sound/{name}")
    public ResponseEntity<Resource> getSound(@PathVariable String name) {
        if (name == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Resource res = new ClassPathResource("/sounds/" + name);
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
                .path(name).encode(StandardCharsets.UTF_8).toUriString());
        headers.setContentLength(length);
        headers.setContentType(new MediaType("audio", "wav"));
        return new ResponseEntity<>(res, headers, HttpStatus.OK);
    }
}
