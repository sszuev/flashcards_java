package com.gitlab.sszuev.flashcards.rest;

import com.gitlab.sszuev.flashcards.dto.CardRecord;
import com.gitlab.sszuev.flashcards.service.CardsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

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
}
