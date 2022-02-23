package com.gitlab.sszuev.flashcards.parser;

import com.gitlab.sszuev.flashcards.domain.Card;
import com.gitlab.sszuev.flashcards.domain.Dictionary;
import com.gitlab.sszuev.flashcards.domain.EntityFactory;
import com.gitlab.sszuev.flashcards.domain.Example;
import com.gitlab.sszuev.flashcards.domain.Language;
import com.gitlab.sszuev.flashcards.domain.Status;
import com.gitlab.sszuev.flashcards.domain.Translation;
import com.gitlab.sszuev.flashcards.domain.User;
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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * To parse XMLs provided by Lingvo Tutor.
 * <p>
 * Created by @ssz on 01.05.2021.
 */
@Component
public class LingvoParser {
    private static final Map<String, StandardLanguage> LANGUAGE_MAP = Map.of(
            "1033", StandardLanguage.EN,
            "1049", StandardLanguage.RU);
    private static final Map<String, StandardPartOfSpeech> PART_OF_SPEECH_MAP = Map.of(
            "1", StandardPartOfSpeech.NOUN,
            "2", StandardPartOfSpeech.ADJECTIVE,
            "3", StandardPartOfSpeech.VERB);
    private static final Map<String, Status> STATUS_MAP = Map.of(
            "2", Status.UNKNOWN,
            "3", Status.IN_PROCESS,
            "4", Status.LEARNED);

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
        return EntityFactory.newDictionary(User.SYSTEM_USER, root.getAttribute("title"), src, dst, parseCardList(root));
    }

    private static Language parseLanguage(Element root, String id) {
        return EntityFactory.newLanguage(WrongDataException.requireNonNull(LANGUAGE_MAP.get(root.getAttribute(id)).name(),
                "Can't find language " + id), null);
    }

    private static List<Card> parseCardList(Element root) {
        return DOMUtils.elements(root, "card").flatMap(LingvoParser::parseMeanings).toList();
    }

    private static Stream<Card> parseMeanings(Element node) {
        String word = DOMUtils.getNormalizedContent(DOMUtils.getElement(node, "word"));
        return DOMUtils.elements(DOMUtils.getElement(node, "meanings"), "meaning")
                .map(n -> parseMeaning(word, n));
    }

    private static Card parseMeaning(String word, Element node) {
        String transcription = node.getAttribute("transcription");
        String id = node.getAttribute("partOfSpeech");
        String pos = Optional.ofNullable(PART_OF_SPEECH_MAP.get(id)).map(Enum::name).orElse(null);
        Element statistics = DOMUtils.getElement(node, "statistics");
        Status status = WrongDataException.requireNonNull(STATUS_MAP.get(WrongDataException
                .requireNonNull(statistics.getAttribute("status"), "no status")), "unknown status");
        Integer answered;
        if (status != Status.LEARNED) {
            answered = Optional.of(statistics.getAttribute("answered"))
                    .filter(x -> x.matches("\\d+")).map(Integer::parseInt).orElse(0);
        } else { // in case of status=4 there is some big number
            answered = null;
        }
        List<Translation> translations = DOMUtils.elements(DOMUtils.getElement(node, "translations"), "word")
                .map(LingvoParser::parseTranslation).toList();
        List<Example> examples = DOMUtils.findElement(node, "examples")
                .map(x -> DOMUtils.elements(x, "example")).orElseGet(Stream::empty)
                .map(LingvoParser::parseExample).toList();
        return EntityFactory.newCard(word,
                transcription, pos, translations, examples, status, answered, "parsed from lingvo xml");
    }

    private static Translation parseTranslation(Element node) {
        return EntityFactory.newTranslation(DOMUtils.getNormalizedContent(node));
    }

    private static Example parseExample(Element node) {
        return EntityFactory.newExample(DOMUtils.getNormalizedContent(node));
    }

}
