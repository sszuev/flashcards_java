package com.gitlab.sszuev.flashcards.documents;

import com.gitlab.sszuev.flashcards.documents.impl.LingvoDocumentParser;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by @ssz on 17.05.2021.
 */
@SpringBootTest
@ContextConfiguration(classes = {LingvoMappings.class, LingvoDocumentParser.class})
public class LingvoDocumentParserTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(LingvoDocumentParserTest.class);

    @Autowired
    private DictionaryParser parser;

    @Test
    public void testParse() throws IOException {
        // word + translations must be unique
        Set<String> cards = new HashSet<>();
        Dictionary dic = load("/data/BusinessEnRu.xml");
        dic.cards().forEach(c -> {
            String w = toKey(c);
            LOGGER.info("{}", w);
            Assertions.assertTrue(cards.add(w));
            Assertions.assertFalse(w.contains("\n"));
        });
        Assertions.assertEquals(242, dic.getCardsCount());
    }

    private String toKey(Card c) {
        return String.format("%s => [%s]", c.getText(),
                c.translations().map(x -> x.getText()).sorted().collect(Collectors.joining(", ")));
    }

    @SuppressWarnings("SameParameterValue")
    private Dictionary load(String name) throws IOException {
        try (InputStream in = LingvoDocumentParserTest.class.getResourceAsStream(name)) {
            return parser.parse(in);
        }
    }

}
