package com.gitlab.sszuev.flashcards.metrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A helper-component to store voice-rss statistics in file-system.
 * It is important to know the exact count (regardless of restarts),
 * since the default voice rss plan offers only 350 request a day.
 * <p>
 * Created by @ssz on 23.05.2021.
 */
@Component
public class CountHolder {
    private final static Logger LOGGER = LoggerFactory.getLogger(CountHolder.class);

    private final Path file;
    private final AtomicLong counter;

    public CountHolder(@Value("${app.speaker.metrics.voice-rss-counter:voice-rss.count}") String file) throws IOException {
        this.file = absolutePath(file);
        this.counter = new AtomicLong(read(this.file));
    }

    @PreDestroy
    public void save() throws IOException {
        write(file, counter.get());
    }

    public long getCount() {
        return counter.get();
    }

    public void setCount(long count) {
        counter.set(count);
    }

    public static Path absolutePath(String file) {
        if (!file.startsWith("/")) {
            file = "/" + file;
        }
        return Paths.get(System.getProperty("user.dir") + file);
    }

    public static long read(Path file) throws IOException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Read count from file {}", file);
        }
        if (!Files.exists(file)) return 0;
        try (InputStream is = Files.newInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(is)) {
            return ois.readLong();
        }
    }

    public static void write(Path file, long count) throws IOException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Write count to file {}", file);
        }
        try (OutputStream os = Files.newOutputStream(file);
             ObjectOutputStream oos = new ObjectOutputStream(os)) {
            oos.writeLong(count);
        }
    }
}
