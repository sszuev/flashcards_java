package com.gitlab.sszuev.flashcards.service;

import com.gitlab.sszuev.flashcards.Compounder;
import com.gitlab.sszuev.flashcards.TextToSpeechService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by @ssz on 09.05.2021.
 */
@SpringBootTest(classes = {LocalResourceTTSImpl.class, Compounder.class})
public class LocalResourceTTSImplTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalResourceTTSImplTest.class);

    private static final String TEST_LANG = "en";
    private static final String TEST_WORD = "weather";
    private static final String TEST_ID = "en:weather.flac";
    private static final long TEST_SIZE = 26395;
    private static final byte[] TEST_MD5 = {-125, 77, -109, -54, 100, -9, -127, 121, -2, 58, -108, -15, -97, -106, 87, 46};

    @Autowired
    private TextToSpeechService service;

    @BeforeEach
    public void ensureValid() {
        Assumptions.assumeTrue(service instanceof LocalResourceTTSImpl);
    }

    @Test
    public void testGetResourceID() {
        String res = service.getResourceID(TEST_WORD, TEST_LANG);
        LOGGER.info("Resource path id: {}", res);
        Assertions.assertNotNull(res);
        Assertions.assertEquals(TEST_ID, res);
    }

    @Test
    public void testGetResource() throws IOException, NoSuchAlgorithmException {
        Resource res = service.getResource(TEST_ID);
        LOGGER.info("Resource: {}", res);
        Assertions.assertNotNull(res);
        Assertions.assertTrue(res.exists());
        Assertions.assertTrue(res instanceof ByteArrayResource);
        Assertions.assertEquals(TEST_SIZE, res.contentLength());
        byte[] array = ((ByteArrayResource) res).getByteArray();

        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(array);

        Assertions.assertArrayEquals(TEST_MD5, md.digest());
    }
}
