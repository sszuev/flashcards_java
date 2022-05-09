package com.gitlab.sszuev.flashcards.documents.impl;

import com.gitlab.sszuev.flashcards.documents.DictionaryParser;
import com.gitlab.sszuev.flashcards.domain.*;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * To parse XMLs provided by Lingvo Tutor.
 * <p>
 * Created by @ssz on 01.05.2021.
 */
@Component
public class LingvoDocumentParser implements DictionaryParser {
    private final LingvoMappings mappings;

    public LingvoDocumentParser(LingvoMappings mappings) {
        this.mappings = Objects.requireNonNull(mappings);
    }

    /**
     * Performs parsing XML.
     *
     * @param resource {@link Resource}, not {@code null}
     * @return {@link Dictionary}
     * @throws RuntimeException - when can't read or parse {@code resource}
     */
    @Override
    public Dictionary parse(Resource resource) {
        try (InputStream in = resource.getInputStream()) {
            return parse(in);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Performs parsing XML.
     * The caller is responsible for closing {@code input}.
     *
     * @param input {@link InputStream}, not {@code null}
     * @return {@link Dictionary}
     * @throws RuntimeException - when can't read or parse {@code input}
     */
    @Override
    public Dictionary parse(InputStream input) {
        return parse(new BufferedReader(new InputStreamReader(Objects.requireNonNull(input), mappings.charset())));
    }

    /**
     * Performs parsing XML.
     * The caller is responsible for closing {@code input}.
     *
     * @param input {@link Reader}, not {@code null}
     * @return {@link Dictionary}
     * @throws RuntimeException - when can't read or parse {@code input}
     */
    @Override
    public Dictionary parse(Reader input) {
        try {
            return loadDictionary(new InputSource(input));
        } catch (ParserConfigurationException | IOException | SAXException ex) {
            throw new IllegalStateException(ex);
        }
    }

    public Dictionary loadDictionary(InputSource in) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(in);
        Element root = doc.getDocumentElement();
        Language src = parseLanguage(root, "sourceLanguageId");
        Language dst = parseLanguage(root, "destinationLanguageId");
        return EntityFactory.newDictionary(User.SYSTEM_USER, root.getAttribute("title"), src, dst, parseCardList(root));
    }

    private Language parseLanguage(Element root, String id) {
        return EntityFactory.newLanguage(mappings.toLanguageTag(root.getAttribute(id)), null);
    }

    private List<Card> parseCardList(Element root) {
        return DOMUtils.elements(root, "card").flatMap(this::parseMeanings).toList();
    }

    private Stream<Card> parseMeanings(Element node) {
        String word = DOMUtils.getNormalizedContent(DOMUtils.getElement(node, "word"));
        return DOMUtils.elements(DOMUtils.getElement(node, "meanings"), "meaning")
                .map(n -> parseMeaning(word, n));
    }

    private Card parseMeaning(String word, Element node) {
        String transcription = node.getAttribute("transcription");
        String id = node.getAttribute("partOfSpeech");
        String pos = id.isBlank() ? null : mappings.toPartOfSpeechTag(id);
        Element statistics = DOMUtils.getElement(node, "statistics");
        Status status = mappings.toStatus(statistics.getAttribute("status"));
        Integer answered;
        if (status != Status.UNKNOWN) {
            answered = Optional.of(statistics.getAttribute("answered"))
                    .filter(x -> x.matches("\\d+")).map(Integer::parseInt).orElse(0);
        } else { // in case of status=4 there is some big number
            answered = null;
        }
        List<Translation> translations = DOMUtils.elements(DOMUtils.getElement(node, "translations"), "word")
                .map(LingvoDocumentParser::parseTranslation).toList();
        List<Example> examples = DOMUtils.findElement(node, "examples")
                .map(x -> DOMUtils.elements(x, "example")).orElseGet(Stream::empty)
                .map(LingvoDocumentParser::parseExample).toList();
        return EntityFactory.newCard(word, transcription, pos, translations, examples, answered, "parsed from lingvo xml");
    }

    private static Translation parseTranslation(Element node) {
        return EntityFactory.newTranslation(DOMUtils.getNormalizedContent(node));
    }

    private static Example parseExample(Element node) {
        return EntityFactory.newExample(DOMUtils.getNormalizedContent(node));
    }

}
