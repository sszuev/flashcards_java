package com.gitlab.sszuev.flashcards.documents.impl;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by @ssz on 02.05.2021.
 */
public class DOMUtils {

    /**
     * Gets element by the specified tag or throws an error.
     *
     * @param parent {@link Element}
     * @param tag    {@code String}
     * @return {@link Element}
     * @throws IllegalStateException if no element found
     */
    public static Element getElement(Element parent, String tag) {
        List<Element> list = getElements(parent, tag);
        if (list.size() != 1) {
            throw new IllegalStateException("Expected single member for tag='" + tag + "'");
        }
        return list.get(0);
    }

    /**
     * Finds element by the specified tag or throws an error.
     *
     * @param parent {@link Element}
     * @param tag    {@code String}
     * @return an {@code Optional} with {@link Element}
     * @throws IllegalStateException if there is more than one element found
     */
    public static Optional<Element> findElement(Element parent, String tag) {
        List<Element> list = getElements(parent, tag);
        if (list.size() == 1) {
            return Optional.of(list.get(0));
        } else if (list.isEmpty()) {
            return Optional.empty();
        }
        throw new IllegalStateException("Expected not more than one member for tag='" + tag + "'");
    }

    /**
     * Returns elements by tag.
     *
     * @param parent {@link Element}
     * @param tag    {@code String}
     * @return a {@code List} of {@link Element}s
     */
    public static List<Element> getElements(Element parent, String tag) {
        return elements(parent, tag).collect(Collectors.toList());
    }

    /**
     * Lists elements by tag.
     *
     * @param parent {@link Element}
     * @param tag    {@code String}
     * @return a {@code Stream} of {@link Element}s
     */
    public static Stream<Element> elements(Element parent, String tag) {
        return children(parent)
                .filter(x -> x instanceof Element).map(x -> (Element) x)
                .filter(x -> Objects.equals(x.getTagName(), tag));
    }

    /**
     * Lists all direct children of the given element.
     *
     * @param parent {@link Element}
     * @return a {@code Stream} of {@link Element}s
     */
    public static <X extends Node> Stream<X> children(Element parent) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(listChildren(parent), Spliterator.ORDERED), false);
    }

    /**
     * Creates an iterator of {@link Node}s.
     *
     * @param parent {@link Element}
     * @param <X>    subtype of {@link Node}
     * @return an {@code Iterator} over {@link X}
     */
    public static <X extends Node> Iterator<X> listChildren(Element parent) {
        NodeList list = parent.getChildNodes();
        int length = list.getLength();
        return new Iterator<>() {
            int index;

            @Override
            public boolean hasNext() {
                return index < length - 1;
            }

            @SuppressWarnings("unchecked")
            @Override
            public X next() {
                return (X) list.item(index++);
            }
        };
    }

    /**
     * Returns a text without leading and trailing spaces and new-line symbols.
     *
     * @param node {@link Element}
     * @return {@code String}
     */
    public static String getNormalizedContent(Element node) {
        return node.getTextContent().trim().replaceAll("\\n+$", "").replaceAll("^\\n+", "");
    }
}
