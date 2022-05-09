package com.gitlab.sszuev.flashcards.documents;

import com.gitlab.sszuev.flashcards.RunConfig;
import com.gitlab.sszuev.flashcards.TestUtils;
import com.gitlab.sszuev.flashcards.documents.impl.LingvoDictionaryWriter;
import com.gitlab.sszuev.flashcards.documents.impl.LingvoMappings;
import com.gitlab.sszuev.flashcards.domain.Card;
import com.gitlab.sszuev.flashcards.domain.Dictionary;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@SpringBootTest
@ContextConfiguration(classes = {RunConfig.class, LingvoMappings.class, LingvoDictionaryWriter.class})
@TestPropertySource(properties = {
        "app.tutor.run.answers=5"
})
public class LingvoDictionaryWriterTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(LingvoDictionaryWriterTest.class);

    @Autowired
    private DictionaryWriter writer;

    @Test
    public void testWrite() {
        String expected = normalize(loadExpectedData("/TestDictionaryEnRu.xml"));

        Card card1 = TestUtils.createCard(42L, "rain", "rein", 2, "noun",
                List.of("дождь", "ливень"),
                List.of("The skies no longer rain death.", "The sockets were filled with rain."), "{}");
        Card card2 = TestUtils.createCard(42L, "mutual", "ˈmjuːʧʊəl", 12, "adjective",
                List.of("взаимный", "обоюдный", "общий", "совместный"),
                List.of("Twenty years of mutual vanity, and nothing more."), "{}");
        Card card3 = TestUtils.createCard(42L, "test", "test", null, "verb", List.of("тестировать"), List.of(), "{}");

        Dictionary dictionary = TestUtils.createDictionary(42L,
                "The Test Dictionary", "en", "ru", List.of(card1, card2, card3));

        StringWriter sw = new StringWriter();
        writer.write(dictionary, sw);

        LOGGER.info("\n{}", sw);
        String actual = normalize(sw.toString());
        Assertions.assertEquals(expected, actual);
    }

    @SuppressWarnings("SameParameterValue")
    private static String loadExpectedData(String resource) {
        try {
            Path p = Paths.get(Objects.requireNonNull(LingvoDictionaryWriterTest.class.getResource(resource)).toURI());
            return Files.readString(p, StandardCharsets.UTF_16);
        } catch (URISyntaxException | IOException e) {
            throw new AssertionError(e);
        }
    }

    private static String normalize(String s) {
        return s.replaceAll("[\n\r\t]", "");
    }
}
