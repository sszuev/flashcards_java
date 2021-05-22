package com.gitlab.sszuev.flashcards.internal;

import com.gitlab.sszuev.flashcards.Compounder;
import org.springframework.core.io.Resource;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A library that combines different sources encapsulated in ordered list.
 * A user receives the first found resource.
 * TODO: add in-memory cache for resources.
 * <p>
 * Created by @ssz on 19.05.2021.
 */
public class ResourceListAudioLibrary implements AudioLibrary {
    private static final Compounder HELPER = new Compounder(":");
    private final List<AudioLibrary> resources;

    public ResourceListAudioLibrary(Collection<Resource> resources,
                                    Function<Resource, ? extends AudioLibrary> factory) {
        this(fromResources(resources, factory));
    }

    public ResourceListAudioLibrary(List<AudioLibrary> resources) {
        this.resources = Objects.requireNonNull(resources);
    }

    public static List<AudioLibrary> fromResources(Collection<Resource> resources,
                                                   Function<Resource, ? extends AudioLibrary> factory) {
        if (resources == null || resources.isEmpty()) {
            throw new IllegalArgumentException();
        }
        List<AudioLibrary> res = resources.stream()
                .filter(Resource::isReadable)
                .map(factory)
                .collect(Collectors.toUnmodifiableList());
        if (res.isEmpty()) {
            throw new IllegalArgumentException("No readable resources have been found in collection=" + resources);
        }
        return res;
    }

    @Override
    public String getResourceID(String text, String... options) {
        if (resources.size() == 1) {
            return resources.get(0).getResourceID(text, options);
        }
        for (int i = 0; i < resources.size(); i++) {
            String id = resources.get(i).getResourceID(text, options);
            if (id != null) {
                return HELPER.compound(String.valueOf(i), id);
            }
        }
        return null;
    }

    @Override
    public Resource getResource(String id) {
        if (resources.size() == 1) {
            return resources.get(0).getResource(id);
        }
        int index = Integer.parseInt(HELPER.getFirst(id));
        return resources.get(index).getResource(HELPER.getRest(id));
    }
}
