package com.gitlab.sszuev.flashcards.documents;

import com.gitlab.sszuev.flashcards.domain.Dictionary;
import org.springframework.core.io.Resource;

import java.io.OutputStream;

public interface DictionaryWriter {

    /**
     * Writes the dictionary into the document representing as {@code resource}.
     *
     * @param dictionary {@link Dictionary}, not {@code null}
     * @return {@link Resource}
     */
    Resource write(Dictionary dictionary);

    /**
     * Writes the dictionary into the document representing as {@code output}.
     * The caller is responsible for closing {@code output}.
     *
     * @param dictionary {@link Dictionary}, not {@code null}
     * @param output     {@link OutputStream}, not {@code null}
     * @throws RuntimeException - something wrong
     */
    void write(Dictionary dictionary, OutputStream output);

}
