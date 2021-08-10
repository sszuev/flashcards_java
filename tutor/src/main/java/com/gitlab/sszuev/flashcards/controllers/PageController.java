package com.gitlab.sszuev.flashcards.controllers;

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
public class PageController {
    private final RunConfig config;

    public PageController(RunConfig config) {
        this.config = Objects.requireNonNull(config);
    }

    @GetMapping("/{path:[^.]*}")
    public String redirect() {
        return "redirect:/";
    }

    @GetMapping(value = {"/"})
    public ModelAndView index() {
        return new ModelAndView("index", Map.of(
                "numberOfWordsToShow", config.getNumberOfWordsToShow(),
                "numberOfWordsPerStage", config.getNumberOfWordsPerStage(),
                "numberOfRightAnswers", config.getNumberOfRightAnswers(),
                "numberOfOptionsPerWord", config.getStageOptionsNumberOfVariantsPerWord() - 1)
        );
    }
}
