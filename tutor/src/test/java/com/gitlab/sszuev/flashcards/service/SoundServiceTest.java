package com.gitlab.sszuev.flashcards.service;

import com.gitlab.sszuev.flashcards.service.impl.SoundServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by @ssz on 08.05.2021.
 */
@SpringBootTest
@ContextConfiguration(classes = SoundServiceImpl.class)
public class SoundServiceTest {
    @Autowired
    private SoundService service;
    @MockBean
    private TextToSpeechService provider;

    @Test
    public void testGetResourceName() {
        String lang = "en";
        String text = "cloudy";
        String fromTTS = "EN:flac/cloudy.flac";
        String expected = "EN:flac%2Fcloudy.flac";

        Mockito.when(provider.getResourceID(Mockito.eq(text), Mockito.eq(lang))).thenReturn(fromTTS);

        Assertions.assertEquals(expected, service.getResourceName(text, lang));
        Assertions.assertNull(service.getResourceName("XXX", lang));
        Assertions.assertNull(service.getResourceName(text, "xx"));
    }

    @Test
    public void testGetResource() {
        String given = "x";
        Resource expected = Mockito.mock(Resource.class);
        Mockito.when(provider.getResource(Mockito.eq(given))).thenReturn(expected);

        Assertions.assertSame(expected, service.getResource(given));
        Assertions.assertNull(service.getResource("XXX"));
    }
}
