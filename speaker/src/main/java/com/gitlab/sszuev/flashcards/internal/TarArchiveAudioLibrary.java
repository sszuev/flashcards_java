package com.gitlab.sszuev.flashcards.internal;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of {@link AudioLibrary} to work with tar archives that contain flac audio files.
 * <p>
 * Flac audio files contain SWAC fields encapsulated in Vorbis Comment tags.
 * For more information about SWAC fields, please visit the SWAC Audio Collection Homepage at: http://shtooka.net/swac
 * <p>
 * Created by @ssz on 19.05.2021.
 */
public class TarArchiveAudioLibrary implements AudioLibrary {
    private static final String ENTITY_NAME_ENCODING = StandardCharsets.UTF_8.name();
    private static final String DIR = "flac/";
    private static final String TEXT_REF = "SWAC_TEXT";
    private static final String INDEXES = DIR + "index.tags.txt";

    private final InputStreamSupplier<TarArchiveInputStream> supplier;
    private volatile Map<String, List<Info>> indexes;

    public TarArchiveAudioLibrary(InputStreamSource supplier) {
        this(fromSource(supplier));
    }

    protected TarArchiveAudioLibrary(InputStreamSupplier<TarArchiveInputStream> supplier) {
        this.supplier = Objects.requireNonNull(supplier);
    }

    public static InputStreamSupplier<TarArchiveInputStream> fromSource(InputStreamSource source) {
        Objects.requireNonNull(source);
        return new InputStreamSupplier<>() {
            @Override
            public TarArchiveInputStream getInputStream() throws IOException {
                return new TarArchiveInputStream(source.getInputStream(), ENTITY_NAME_ENCODING);
            }

            @Override
            public String toString() {
                return "FromSource:" + source;
            }
        };
    }

    public static String parseTextIndex(String block) {
        List<String> res = Arrays.stream(block.split("\r*\n"))
                .map(x -> {
                    String[] arr = x.split("=");
                    return arr.length == 2 && TEXT_REF.equals(arr[0]) ? arr[1] : null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (res.size() == 1) {
            return res.get(0);
        }
        throw new IllegalStateException("Can't find " + TEXT_REF + " in <" + block + ">.");
    }

    public static String parseFileIndex(String block) {
        List<String> res = Arrays.stream(block.split("\r*\n"))
                .map(x -> x.startsWith("[") && x.endsWith("]") ? x.replaceAll("^\\[(.+)]$", "$1") : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (res.size() == 1) {
            return res.get(0);
        }
        throw new IllegalStateException("Can't find file in <" + block + ">.");
    }

    public Map<String, List<Info>> getIndexMap() {
        if (indexes != null) return indexes;
        synchronized (this) {
            if (indexes != null) return indexes;
            return indexes = readIndexMap();
        }
    }

    private Map<String, List<Info>> readIndexMap() {
        String map = new String(readEntry(INDEXES), StandardCharsets.UTF_8);
        return Arrays.stream(map.split("\r*\n\r*\n"))
                .filter(x -> x.contains(TEXT_REF))
                .collect(Collectors.toMap(TarArchiveAudioLibrary::parseTextIndex
                        , s -> List.of(new Info(parseTextIndex(s), parseFileIndex(s)))
                        , (left, right) -> {
                            List<Info> res = new ArrayList<>(left.size() + right.size());
                            res.addAll(left);
                            res.addAll(right);
                            return res;
                        }));
    }

    private byte[] readEntry(String name) {
        try (TarArchiveInputStream tar = supplier.getInputStream()) {
            TarArchiveEntry entry;
            while ((entry = tar.getNextTarEntry()) != null) {
                if (!name.equals(entry.getName())) {
                    continue;
                }
                long size = entry.getRealSize();
                if (size >= Integer.MAX_VALUE) {
                    throw new IllegalStateException();
                }
                byte[] res = new byte[(int) size];
                if (IOUtils.readFully(tar, res) < 0) {
                    throw new IllegalStateException();
                }
                return res;
            }
            throw new IllegalArgumentException("Can't find entry '" + name + "'");
        } catch (Exception e) {
            throw new IllegalStateException("Unexpected error while [" + supplier + "]", e);
        }
    }

    @Override
    public String getResourceID(String text, String... options) {
        List<Info> res = getIndexMap().get(text);
        if (res == null || res.isEmpty()) return null;
        return res.get(0).file;
    }

    @Override
    public Resource getResource(String file) {
        return new ByteArrayResource(readEntry(DIR + file));
    }

    public static class Info {
        private final String text;
        private final String file;

        protected Info(String text, String file) {
            this.text = Objects.requireNonNull(text);
            this.file = Objects.requireNonNull(file);
        }

        @Override
        public String toString() {
            return String.format("{'%s'[%s]}", text, file);
        }
    }

    @SuppressWarnings("NullableProblems")
    public interface InputStreamSupplier<S extends InputStream> extends InputStreamSource {
        @Override
        S getInputStream() throws IOException;
    }
}
