package com.gitlab.sszuev.flashcards;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;

/**
 * Describes test-session (run) configuration.
 */
@Validated
@Configuration
public class RunConfig {

    @Value("${app.tutor.run.words-for-show:10}")
    private int numberOfWordsToShow;
    @Value("${app.tutor.run.words-for-test:5}")
    private int numberOfWordsPerStage;
    @Value("${app.tutor.run.answers:10}")
    private int numberOfRightAnswers;
    @Value("${app.tutor.run.stage-options.variants:5}")
    private int stageOptionsNumberOfVariantsPerWord;

    /**
     * Returns the number of words on the first non-test show-stage.
     *
     * @return positive {@code int}
     */
    @Min(1)
    public int getNumberOfWordsToShow() {
        return numberOfWordsToShow;
    }

    /**
     * Returns the number of words to display per test stage.
     *
     * @return positive {@code int}
     * @see com.gitlab.sszuev.flashcards.dto.Stage
     */
    @Min(1)
    public int getNumberOfWordsPerStage() {
        return numberOfWordsPerStage;
    }

    /**
     * Returns the number of correct answers after which the word is considered learned.
     *
     * @return positive {@code int}
     */
    @Min(1)
    public int getNumberOfRightAnswers() {
        return numberOfRightAnswers;
    }

    /**
     * Returns the number of answer variants per word in the stage "options".
     * Note that this is an approximate setting, exact number of variants on the right panel may differ.
     * Also note, it includes the right answer as well.
     *
     * @return positive {@code int} (must be greater then {@code 1})
     */
    @Min(2)
    public int getStageOptionsNumberOfVariantsPerWord() {
        return stageOptionsNumberOfVariantsPerWord;
    }
}
