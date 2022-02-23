package com.gitlab.sszuev.flashcards.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BiPredicate;

/**
 * Created by @ssz on 13.06.2021.
 */
public class CollectionUtils {

    /**
     * This method tries to select only unique (non-similar) random items from the given {@code source} collection,
     * using the specified random {@code generator} and test-predicate to decide is the item unique or not.
     * If it is impossible to select unique elements
     * the method returns just randomized subset of the original collection.
     *
     * @param source     a {@link List} of {@link X}-items
     * @param resultSize a {@code int}-size of result collection
     * @param generator  a {@link Random} - generator
     * @param isSimilar  a {@link BiPredicate} to decide if two input elements are similar or not
     * @param <X>        any type
     * @return a {@code Collection} of {@link X}s
     */
    public static <X> Collection<X> trySelectUniqueRandomItems(List<X> source,
                                                               int resultSize,
                                                               Random generator,
                                                               BiPredicate<X, X> isSimilar) {
        if (source.size() < resultSize) {
            throw new IllegalArgumentException();
        }
        List<X> src = new ArrayList<>(source);
        // it's okay small collection:
        Collections.shuffle(src, generator);
        if (source.size() == resultSize) {
            return src;
        }
        List<X> res = new ArrayList<>(resultSize);
        for (X item : src) {
            if (!testIsSimilar(res, item, isSimilar)) {
                res.add(item);
            }
            if (res.size() == resultSize) {
                return res;
            }
        }
        for (int i = src.size() - 1; i >= 0; i--) {
            res.add(src.get(i));
            if (res.size() == resultSize) {
                return res;
            }
        }
        throw new IllegalStateException();
    }

    /**
     * Answers {@code true} if the given test element is similar with some element from the specified collection.
     *
     * @param list      a {@code List} of {@link X}s
     * @param test      a {@link X}-element to test
     * @param isSimilar a {@link BiPredicate} to decide if two input elements are similar or not
     * @param <X>       any type
     * @return {@code boolean}
     */
    public static <X> boolean testIsSimilar(List<X> list, X test, BiPredicate<X, X> isSimilar) {
        for (X e : list) {
            if (isSimilar.test(e, test)) {
                return true;
            }
        }
        return false;
    }

    public static <K, V> void addAll(Map<K, List<V>> base, Map<K, V> add) {
        add.forEach((k, v) -> base.computeIfAbsent(k, x -> new ArrayList<>()).add(v));
    }
}
