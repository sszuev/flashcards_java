package com.gitlab.sszuev.flashcards.service;

import com.gitlab.sszuev.flashcards.Compounder;
import com.gitlab.sszuev.flashcards.TextToSpeechService;
import com.gitlab.sszuev.flashcards.internal.AudioLibrary;
import com.gitlab.sszuev.flashcards.internal.ResourceListAudioLibrary;
import com.gitlab.sszuev.flashcards.internal.TarArchiveAudioLibrary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * To work with local libraries of audio-resources.
 * TODO: include external tts service
 * <p>
 * Created by @ssz on 08.05.2021.
 */
@Service
public class LocalResourceTTSImpl implements TextToSpeechService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalResourceTTSImpl.class);

    private static final String RESOURCE_DIR = "";
    private static final String LIB_PATTERN = "classpath*:%s/*/*.tar";
    private final Compounder compounder;
    private final Map<String, AudioLibrary> libraries;

    @Autowired
    public LocalResourceTTSImpl(ResourcePatternResolver resolver, Compounder compounder) throws IOException {
        this(compounder, loadLibraries(resolver, RESOURCE_DIR));
    }

    protected LocalResourceTTSImpl(Compounder compounder, Map<String, AudioLibrary> libraries) {
        this.compounder = Objects.requireNonNull(compounder);
        this.libraries = Collections.unmodifiableMap(Objects.requireNonNull(libraries));
    }

    public static Map<String, AudioLibrary> loadLibraries(ResourcePatternResolver resolver, String dir) throws IOException {
        Function<Resource, TarArchiveAudioLibrary> factory = TarArchiveAudioLibrary::new;
        return loadLibraryResources(resolver, dir).entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, e -> new ResourceListAudioLibrary(e.getValue(), factory)));
    }

    public static Map<String, Set<Resource>> loadLibraryResources(ResourcePatternResolver resolver, String dir) throws IOException {
        Resource[] resources = resolver.getResources(String.format(LIB_PATTERN, dir));
        if (resources.length == 0) {
            LOGGER.warn("No files [*.tar] are found in the directory [{}]", dir);
            return Map.of();
        }
        Map<String, Set<Resource>> res = new HashMap<>();
        for (Resource r : resources) {
            String file = r.getFilename();
            if (file == null || !r.isReadable()) {
                LOGGER.warn("WTF {}:::{}, {}, {}", r, r.isReadable(), r.isFile(), file);
                continue;
            }
            LOGGER.info("Load audio library {}", r);
            String k = getParentDir(r);
            res.computeIfAbsent(k.toLowerCase(), x -> new HashSet<>()).add(r);
        }
        if (res.isEmpty()) {
            throw new IllegalArgumentException("Can't find valid *.tar files in the directory " + dir + ".");
        }
        return res;
    }

    private static String getParentDir(Resource resource) {
        try {
            // URI and File accessors do not work in general case.
            URL url = Objects.requireNonNull(resource.getURL(), "Null URL for <" + resource + ">.");
            return url.toString().replaceAll(".+/([^/]+)/[^/]+$", "$1");
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to parse parent for <" + resource + ">.", ex);
        }
    }

    @Override
    public String getResourceID(String text, String lang, String... options) {
        AudioLibrary lib = getLibrary(lang);
        if (lib == null) return null;
        String res = lib.getResourceID(text, options);
        return res != null ? compounder.create(lang, res) : null;
    }

    @Override
    public Resource getResource(String id) {
        return getLibrary(compounder.getFirst(id)).getResource(compounder.getRest(id));
    }

    public AudioLibrary getLibrary(String lang) {
        return libraries.get(Objects.requireNonNull(lang).toLowerCase());
    }

}
