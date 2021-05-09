package com.gitlab.sszuev.flashcards.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * Created by @ssz on 09.05.2021.
 */
public class TextToSpeechServiceTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(TextToSpeechServiceTest.class);
    private TextToSpeechService service;

    @BeforeEach
    public void init() {
        service = new TextToSpeechServiceImpl(new PathMatchingResourcePatternResolver());
    }

    @Test
    public void testGetResourceID(){
        String res = service.getResourceID("weather", "en");
        LOGGER.info("Resource path id: {}", res);
        Assertions.assertNotNull(res);
        Assertions.assertFalse(res.isEmpty());
    }

    @Test
    public void testGetResource(){
        Resource res = service.getResource("weather.wav");
        LOGGER.info("Resource: {}", res);
        Assertions.assertNotNull(res);
        Assertions.assertTrue(res.exists());
        Assertions.assertTrue(res instanceof ClassPathResource);
    }
}
