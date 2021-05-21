package com.gitlab.sszuev.flashcards;

import org.springframework.core.io.Resource;

/**
 * A common interface that provides access to audio resources.
 * <p>
 * Created by @ssz on 21.05.2021.
 */
public interface AudioResourceProvider {
    /**
     * Returns a (Spring) {@code Resource} with audio stream by the specified resource identifier.
     *
     * @param id {@code String} the resource path identifier, not {@code null}
     * @return {@link Resource}, never {@code null}
     * @throws RuntimeException in case no resource found or any other error occurred
     */
    Resource getResource(String id);
}
