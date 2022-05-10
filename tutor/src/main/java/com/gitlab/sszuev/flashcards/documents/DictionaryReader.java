package com.gitlab.sszuev.flashcards.documents;

import com.gitlab.sszuev.flashcards.domain.Dictionary;
import org.springframework.core.io.Resource;

import java.io.InputStream;

public interface DictionaryReader {

    /**
     * Parses the document representing the result as a dictionary.
     *
     * @param resource {@link Resource}, not {@code null}
     * @return {@link Dictionary}
     * @throws RuntimeException - when can't read or parse {@code resource}
     */
    Dictionary parse(Resource resource);

    /**
     * Parses the document representing the result as a dictionary.
     * The caller is responsible for closing {@code input}.
     *
     * @param input {@link InputStream}, not {@code null}
     * @return {@link Dictionary}
     * @throws RuntimeException - when can't read or parse {@code input}
     */
    Dictionary parse(InputStream input);

}
