package com.gitlab.sszuev.flashcards.service;

import com.gitlab.sszuev.flashcards.Compounder;
import com.gitlab.sszuev.flashcards.TextToSpeechService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

/**
 * Created by @ssz on 22.05.2021.
 */
@SpringBootTest(classes = {VoiceRSSClientTTSImpl.class, Compounder.class})
@TestPropertySource(properties = {
        "app.speaker.voicerss.api=" + VoiceRSSClientTTSImplTest.TEST_API,
        "app.speaker.voicerss.format=" + VoiceRSSClientTTSImplTest.TEST_FORMAT,
        "app.speaker.voicerss.key=" + VoiceRSSClientTTSImplTest.TEST_KEY
})
public class VoiceRSSClientTTSImplTest {

    static final String TEST_API = "the.test.api";
    static final String TEST_KEY = "XXX";
    static final String TEST_FORMAT = "wow";

    @SuppressWarnings("HttpUrlsUsage")
    private static final String TEST_TEMPLATE = String.format("http://%s?key=%s&f=%s&hl=%%s&src=%%s",
            TEST_API, TEST_KEY, TEST_FORMAT);

    @MockBean
    private RestTemplate template;
    @Autowired
    private TextToSpeechService service;

    @BeforeEach
    public void ensureValid() {
        Assumptions.assumeTrue(service instanceof VoiceRSSClientTTSImpl);
    }

    @Test
    public void testGetResourceID() {
        String res1 = service.getResourceID("zzz", "zh", "some-op");
        Assertions.assertEquals("zh-cn:zzz", res1);

        String res2 = service.getResourceID("xxx", "zh-tw");
        Assertions.assertEquals("zh-tw:xxx", res2);

        Assertions.assertThrows(IllegalArgumentException.class, () -> service.getResourceID("test", "zzz"));
    }

    @Test
    public void testGetResource() {
        String lang = "unknown-lang";
        String text = "some text";
        byte[] data = new byte[]{42};

        Mockito.when(template.exchange(Mockito.anyString(),
                Mockito.eq(HttpMethod.GET), Mockito.any(HttpEntity.class), Mockito.eq(byte[].class)))
                .thenAnswer(i -> {
                    String url = i.getArgument(0);
                    String queryText = text.replace(" ", "%20");
                    Assertions.assertEquals(String.format(TEST_TEMPLATE, lang, queryText), url);
                    return new ResponseEntity<>(data, HttpStatus.OK);
                });

        Resource res = service.getResource(lang + ":" + text);
        Assertions.assertTrue(res instanceof ByteArrayResource);
        Assertions.assertArrayEquals(data, ((ByteArrayResource) res).getByteArray());
    }
}
