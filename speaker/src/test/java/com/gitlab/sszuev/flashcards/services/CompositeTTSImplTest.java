package com.gitlab.sszuev.flashcards.services;

import com.gitlab.sszuev.flashcards.Compounder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;

/**
 * Created by @ssz on 23.05.2021.
 */
@SpringBootTest(classes = {CompositeTTSImpl.class, Compounder.class})
public class CompositeTTSImplTest {
    @MockBean
    private LocalResourceTTSImpl localResourceTTS;
    @MockBean
    private VoiceRSSClientTTSImpl voiceRSSClientTTS;
    @Autowired
    private CompositeTTSImpl service;

    @Test
    public void testGetResourceID() {
        String text = "XXX";
        String lang = "LL";
        String res = "YYY";
        Mockito.when(localResourceTTS.getResourceID(Mockito.eq(text), Mockito.eq(lang))).thenReturn(res);
        Mockito.when(voiceRSSClientTTS.getResourceID(Mockito.eq(text), Mockito.eq(lang))).thenThrow(new AssertionError());

        Assertions.assertEquals("local:" + res, service.getResourceID(text, lang));

        Mockito.when(localResourceTTS.getResourceID(Mockito.eq(text), Mockito.eq(lang))).thenReturn(null);
        Mockito.when(voiceRSSClientTTS.getResourceID(Mockito.eq(text), Mockito.eq(lang))).thenReturn(res);

        Assertions.assertEquals("external:" + res, service.getResourceID(text, lang));
    }

    @Test
    public void testGetResource() {
        String id = "zzz";
        ByteArrayResource res1 = new ByteArrayResource(new byte[]{42});
        ByteArrayResource res2 = new ByteArrayResource(new byte[]{-42});

        Mockito.when(localResourceTTS.getResource(Mockito.eq(id))).thenReturn(res1);
        Mockito.when(voiceRSSClientTTS.getResource(Mockito.eq(id))).thenReturn(res2);

        Assertions.assertEquals(res2, service.getResource("external:" + id));
        Assertions.assertEquals(res1, service.getResource("local:" + id));
        Assertions.assertThrows(RuntimeException.class, () -> service.getResource("some:id"));
    }
}
