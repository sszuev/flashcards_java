package com.gitlab.sszuev.flashcards;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Describes test-session (run) configuration.
 */
@Configuration
public class RunConfig {

    @Value("${app.tutor.run.words-for-show:10}")
    private int numberOfWordsToShow;
    @Value("${app.tutor.run.words-for-test:5}")
    private int numberOfWordsPerStage;
    @Value("${app.tutor.run.answers:10}")
    private int numberOfRightAnswers;

    /**
     * Returns the number of words on the first non-test show-stage.
     *
     * @return positive {@code int}
     */
    public int getNumberOfWordsToShow() {
        return numberOfWordsToShow;
    }

    /**
     * Returns the number of words to display per test stage.
     *
     * @return positive {@code int}
     * @see com.gitlab.sszuev.flashcards.dto.Stage
     */
    public int getNumberOfWordsPerStage() {
        return numberOfWordsPerStage;
    }

    /**
     * Returns the number of correct answers after which the word is considered learned.
     *
     * @return positive {@code int}
     */
    public int getNumberOfRightAnswers() {
        return numberOfRightAnswers;
    }
}
