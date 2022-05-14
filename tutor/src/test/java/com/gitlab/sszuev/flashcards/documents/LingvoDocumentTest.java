package com.gitlab.sszuev.flashcards.documents;

import com.gitlab.sszuev.flashcards.RunConfig;
import com.gitlab.sszuev.flashcards.TestUtils;
import com.gitlab.sszuev.flashcards.documents.impl.LingvoDictionaryReader;
import com.gitlab.sszuev.flashcards.documents.impl.LingvoDictionaryWriter;
import com.gitlab.sszuev.flashcards.documents.impl.LingvoMappings;
import com.gitlab.sszuev.flashcards.domain.Card;
import com.gitlab.sszuev.flashcards.domain.Dictionary;
import com.gitlab.sszuev.flashcards.domain.Example;
import com.gitlab.sszuev.flashcards.domain.Translation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by @ssz on 17.05.2021.
 */
@SpringBootTest
@ContextConfiguration(classes = {
        RunConfig.class,
        LingvoMappings.class,
        LingvoDictionaryReader.class,
        LingvoDictionaryWriter.class
})
@TestPropertySource(properties = {
        "app.tutor.run.answers=5"
})
public class LingvoDocumentTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(LingvoDocumentTest.class);

    @Autowired
    private DictionaryReader parser;
    @Autowired
    private DictionaryWriter writer;

    @Test
    public void testWrite() {
        String expected = normalize(readDataAsString("/TestDictionaryEnRu.xml"));

        Card card1 = TestUtils.createCard(42L, "rain", "rein", 2, "noun",
                List.of("дождь", "ливень"),
                List.of("The skies no longer rain death.", "The sockets were filled with rain."), "{}");
        Card card2 = TestUtils.createCard(42L, "mutual", "ˈmjuːʧʊəl", 12, "adjective",
                List.of("взаимный", "обоюдный", "общий", "совместный"),
                List.of("Twenty years of mutual vanity, and nothing more."), "{}");
        Card card3 = TestUtils.createCard(42L, "test", "test", null, "verb", List.of("тестировать"), List.of(), "{}");

        Dictionary dictionary = TestUtils.createDictionary(42L,
                "The Test Dictionary", "en", "ru", List.of(card1, card2, card3));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        writer.write(dictionary, out);

        String txt = out.toString(StandardCharsets.UTF_16);

        LOGGER.info("\n{}", txt);
        String actual = normalize(txt);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testParse() throws IOException {
        // word + translations must be unique
        Set<String> cards = new HashSet<>();
        Dictionary dic = readResourceDictionary("/data/BusinessEnRu.xml");
        dic.cards().forEach(c -> {
            String w = toKey(c);
            LOGGER.info("{}", w);
            Assertions.assertTrue(cards.add(w));
            Assertions.assertFalse(w.contains("\n"));
        });
        Assertions.assertEquals(242, dic.getCardsCount());
    }

    @Test
    public void testRoundTrip(@TempDir Path dir) throws IOException {
        Dictionary orig = readResourceDictionary("/data/WeatherEnRu.xml");

        Path tmp = dir.resolve("test-WeatherEnRu.xml");
        try (OutputStream out = Files.newOutputStream(tmp)) {
            writer.write(orig, out);
        }

        Dictionary reload;
        try (InputStream in = Files.newInputStream(tmp)) {
            reload = parser.parse(in);
        }
        assertDictionary(orig, reload);
    }

    private static void assertDictionary(Dictionary expected, Dictionary actual) {
        Assertions.assertEquals(expected.getName(), actual.getName());
        Assertions.assertEquals(expected.getCardsCount(), actual.getCardsCount());
        Assertions.assertEquals(expected.getTargetLanguage().getID(), actual.getTargetLanguage().getID());
        Assertions.assertEquals(expected.getSourceLanguage().getID(), actual.getSourceLanguage().getID());

        List<Card> expectedCards = expected.getCards();
        List<Card> actualCards = actual.getCards();

        for (int i = 0; i < expectedCards.size(); i++) {
            Card expectedCard = expectedCards.get(i);
            Card actualCard = actualCards.get(i);

            Assertions.assertEquals(expectedCard.getText(), actualCard.getText());
            Assertions.assertEquals(expectedCard.getTranscription(), actualCard.getTranscription());
            Assertions.assertEquals(expectedCard.getDetails(), actualCard.getDetails());
            Assertions.assertEquals(expectedCard.getPartOfSpeech(), actualCard.getPartOfSpeech());
            Assertions.assertEquals(expectedCard.getAnswered(), actualCard.getAnswered());

            List<Translation> origTranslations = expectedCard.translations().toList();
            List<Translation> actualTranslations = actualCard.translations().toList();
            Assertions.assertEquals(origTranslations.size(), actualTranslations.size());

            List<Example> expectedExamples = expectedCard.examples().toList();
            List<Example> actualExamples = actualCard.examples().toList();
            Assertions.assertEquals(expectedExamples.size(), actualExamples.size());

            for (int j = 0; j < origTranslations.size(); j++) {
                Translation expectedTranslation = origTranslations.get(j);
                Translation actualTranslation = actualTranslations.get(j);
                Assertions.assertEquals(expectedTranslation.getText(), actualTranslation.getText());
            }

            for (int j = 0; j < expectedExamples.size(); j++) {
                Example expectedExample = expectedExamples.get(j);
                Example actualExample = actualExamples.get(j);
                Assertions.assertEquals(expectedExample.getText(), actualExample.getText());
            }
        }

    }

    private static String toKey(Card c) {
        return String.format("%s => [%s]", c.getText(),
                c.translations().map(x -> x.getText()).sorted().collect(Collectors.joining(", ")));
    }

    private Dictionary readResourceDictionary(String name) throws IOException {
        try (InputStream in = LingvoDocumentTest.class.getResourceAsStream(name)) {
            return parser.parse(in);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private static String readDataAsString(String resource) {
        try {
            Path p = Paths.get(Objects.requireNonNull(LingvoDocumentTest.class.getResource(resource)).toURI());
            return Files.readString(p, StandardCharsets.UTF_16);
        } catch (URISyntaxException | IOException e) {
            throw new AssertionError(e);
        }
    }

    private static String normalize(String s) {
        return s.replaceAll("[\n\r\t]", "");
    }

}
