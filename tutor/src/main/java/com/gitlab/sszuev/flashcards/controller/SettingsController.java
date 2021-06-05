package com.gitlab.sszuev.flashcards.controller;

import com.gitlab.sszuev.flashcards.RunConfig;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;
import java.util.Objects;

/**
 * Created by @ssz on 31.05.2021.
 */
@Controller
public class SettingsController {
    private final RunConfig config;

    public SettingsController(RunConfig config) {
        this.config = Objects.requireNonNull(config);
    }

    @GetMapping(value = {"/"})
    public ModelAndView getRunSettings() {
        return new ModelAndView("index", Map.of(
                "numberOfWordsToShow", config.getNumberOfWordsToShow(),
                "numberOfWordsPerStage", config.getNumberOfWordsPerStage(),
                "numberOfRightAnswers", config.getNumberOfRightAnswers())
        );
    }
}
