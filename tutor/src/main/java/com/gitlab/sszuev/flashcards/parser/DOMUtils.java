package com.gitlab.sszuev.flashcards.parser;

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

    public static Element getElement(Element parent, String tag) {
        List<Element> list = getElements(parent, tag);
        if (list.size() != 1) {
            throw new WrongDataException("Expected single member for tag='" + tag + "'");
        }
        return list.get(0);
    }

    public static Optional<Element> findElement(Element parent, String tag) {
        List<Element> list = getElements(parent, tag);
        if (list.size() == 1) {
            return Optional.of(list.get(0));
        } else if (list.isEmpty()) {
            return Optional.empty();
        }
        throw new WrongDataException("Expected not more than one member for tag='" + tag + "'");
    }

    public static List<Element> getElements(Element parent, String tag) {
        return elements(parent, tag).collect(Collectors.toList());
    }

    public static Stream<Element> elements(Element parent, String tag) {
        return children(parent)
                .filter(x -> x instanceof Element).map(x -> (Element) x)
                .filter(x -> Objects.equals(x.getTagName(), tag));
    }

    public static <X extends Node> Stream<X> children(Element parent) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(listChildren(parent), Spliterator.ORDERED), false);
    }

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
}
