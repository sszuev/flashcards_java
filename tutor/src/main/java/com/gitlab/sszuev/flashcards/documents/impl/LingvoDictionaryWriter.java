package com.gitlab.sszuev.flashcards.documents.impl;

import com.gitlab.sszuev.flashcards.RunConfig;
import com.gitlab.sszuev.flashcards.documents.DictionaryWriter;
import com.gitlab.sszuev.flashcards.domain.Card;
import com.gitlab.sszuev.flashcards.domain.Dictionary;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.Objects;
import java.util.Optional;

@Component
public class LingvoDictionaryWriter implements DictionaryWriter {

    private final LingvoMappings mappings;
    private final RunConfig config;

    public LingvoDictionaryWriter(LingvoMappings mappings, RunConfig config) {
        this.mappings = Objects.requireNonNull(mappings);
        this.config = Objects.requireNonNull(config);
    }

    @Override
    public void write(Dictionary dictionary, Writer writer) {
        Document document = toDocument(dictionary);
        Transformer transformer = createTransformer();
        try {
            transformer.transform(new DOMSource(document), new StreamResult(writer));
        } catch (TransformerException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void write(Dictionary dictionary, OutputStream out) {
        write(dictionary, new OutputStreamWriter(out, mappings.charset()));
    }

    @Override
    public Resource write(Dictionary dictionary) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (Writer w = new OutputStreamWriter(out, mappings.charset())) {
            write(dictionary, w);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
        return new ByteArrayResource(out.toByteArray());
    }

    public Document toDocument(Dictionary dictionary) {
        Document res = newDocument();
        Element root = res.createElement("dictionary");
        res.appendChild(root);
        root.setAttribute("title", dictionary.getName());
        String src = mappings.fromLanguageTag(dictionary.getSourceLanguage().getID());
        String dst = mappings.fromLanguageTag(dictionary.getTargetLanguage().getID());
        root.setAttribute("sourceLanguageId", src);
        root.setAttribute("destinationLanguageId", dst);
        root.setAttribute("targetNamespace", "http://www.abbyy.com/TutorDictionary");
        root.setAttribute("formatVersion", "6");
        root.setAttribute("nextWordId", "42");
        dictionary.cards().forEach(card -> {
            Element element = res.createElement("card");
            root.appendChild(element);
            writeCard(element, card);
        });
        return res;
    }

    private void writeCard(Element parent, Card card) {
        Document doc = parent.getOwnerDocument();
        writeWord(parent, card.getText());

        Element meanings = doc.createElement("meanings");
        parent.appendChild(meanings);

        Element meaning = doc.createElement("meaning");
        meanings.appendChild(meaning);

        Optional.ofNullable(card.getPartOfSpeech()).map(mappings::fromPartOfSpeechTag)
                .ifPresent(x -> meaning.setAttribute("partOfSpeech", x));
        Optional.ofNullable(card.getTranscription()).ifPresent(x -> meaning.setAttribute("transcription", x));
        writeMeaningStatistics(meaning, card);

        Element translations = doc.createElement("translations");
        meaning.appendChild(translations);
        card.translations().forEach(x -> writeWord(translations, x.getText()));

        Element examples = doc.createElement("examples");
        meaning.appendChild(examples);
        card.examples().forEach(x -> writeText(examples, "example", x.getText()));
    }

    private void writeMeaningStatistics(Element meaning, Card card) {
        Document doc = meaning.getOwnerDocument();
        Element statistics = doc.createElement("statistics");
        meaning.appendChild(statistics);

        Status status;
        Integer answered = card.getAnswered();
        if (answered == null) {
            status = Status.UNKNOWN;
        } else {
            if (answered >= config.getNumberOfRightAnswers()) {
                status = Status.LEARNED;
            } else {
                status = Status.IN_PROCESS;
            }
            statistics.setAttribute("answered", answered.toString());
        }
        statistics.setAttribute("status", mappings.fromStatus(status));
    }

    private void writeWord(Element parent, String txt) {
        writeText(parent, "word", txt);
    }

    private void writeText(Element parent, String tag, String txt) {
        Element word = parent.getOwnerDocument().createElement(tag);
        word.setTextContent(txt);
        parent.appendChild(word);
    }

    private Document newDocument() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            throw new RuntimeException(ex);
        }
        return builder.newDocument();
    }

    private Transformer createTransformer() {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = tf.newTransformer();
        } catch (TransformerConfigurationException ex) {
            throw new IllegalStateException(ex);
        }
        transformer.setOutputProperty(OutputKeys.ENCODING, mappings.charset().name());
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        return transformer;
    }
}
