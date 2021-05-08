package com.gitlab.sszuev.flashcards.parser;

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
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * To parse XMLs provided by Lingvo Tutor.
 * <p>
 * Created by @ssz on 01.05.2021.
 */
@Component
public class LingvoParser {
    private static final Map<String, Language> LANGUAGE_MAP = Map.of(
            "1033", StandardLanguage.EN,
            "1049", StandardLanguage.RU);
    private static final Map<String, PartOfSpeech> PART_OF_SPEECH_MAP = Map.of(
            "1", StandardPartOfSpeech.NOUN,
            "2", StandardPartOfSpeech.ADJECTIVE,
            "3", StandardPartOfSpeech.VERB);

    /**
     * Performs parsing XML.
     *
     * @param resource {@link Resource}, not {@code null}
     * @return {@link Dictionary}
     * @throws WrongDataException - when can't read or parse {@code resource}
     */
    public Dictionary parse(Resource resource) {
        try (InputStream in = resource.getInputStream()) {
            return parse(in);
        } catch (IOException e) {
            throw new WrongDataException(e);
        }
    }

    /**
     * Performs parsing XML.
     * The caller is responsible for closing {@code input}.
     *
     * @param input {@link InputStream}, not {@code null}
     * @return {@link Dictionary}
     * @throws WrongDataException - when can't read or parse {@code input}
     */
    public Dictionary parse(InputStream input) {
        return parse(new BufferedReader(new InputStreamReader(Objects.requireNonNull(input), StandardCharsets.UTF_16)));
    }

    /**
     * Performs parsing XML.
     * The caller is responsible for closing {@code input}.
     *
     * @param input {@link Reader}, not {@code null}
     * @return {@link Dictionary}
     * @throws WrongDataException - when can't read or parse {@code input}
     */
    public Dictionary parse(Reader input) {
        try {
            return loadDictionary(new InputSource(input));
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new WrongDataException(e);
        }
    }

    public static Dictionary loadDictionary(InputSource in) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(in);
        Element root = doc.getDocumentElement();
        Language src = parseLanguage(root, "sourceLanguageId");
        Language dst = parseLanguage(root, "destinationLanguageId");
        return new Dictionary(root.getAttribute("title"), src, dst, parseCards(root));
    }

    private static Language parseLanguage(Element root, String id) {
        return WrongDataException.requireNonNull(LANGUAGE_MAP.get(root.getAttribute(id)), "Can't find language " + id);
    }

    private static List<Card> parseCards(Element root) {
        return DOMUtils.elements(root, "card").map(LingvoParser::parseCard).collect(Collectors.toUnmodifiableList());
    }

    private static Card parseCard(Element node) {
        String word = DOMUtils.getElement(node, "word").getTextContent();
        List<Meaning> meanings = DOMUtils.elements(DOMUtils.getElement(node, "meanings"), "meaning")
                .map(LingvoParser::parseMeaning).collect(Collectors.toUnmodifiableList());
        return new Card(word, meanings);
    }

    private static Meaning parseMeaning(Element node) {
        String transcription = node.getAttribute("transcription");
        String id = node.getAttribute("partOfSpeech");
        PartOfSpeech pos = id == null ? null : PART_OF_SPEECH_MAP.get(id);
        List<Translation> translations = DOMUtils.elements(DOMUtils.getElement(node, "translations"), "word")
                .map(LingvoParser::parseTranslation).collect(Collectors.toUnmodifiableList());
        List<Example> examples = DOMUtils.findElement(node, "examples")
                .map(x -> DOMUtils.elements(x, "example")).orElseGet(Stream::empty)
                .map(LingvoParser::parseExample).collect(Collectors.toUnmodifiableList());
        return new Meaning(transcription, pos, translations, examples);
    }

    private static Translation parseTranslation(Element node) {
        return new Translation(node.getTextContent());
    }

    private static Example parseExample(Element node) {
        return new Example(node.getTextContent());
    }

}