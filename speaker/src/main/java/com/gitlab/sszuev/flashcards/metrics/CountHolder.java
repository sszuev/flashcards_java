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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A helper-component to store voice-rss statistics in file-system.
 * It is important to know the exact count (regardless of restarts),
 * since the default voice rss plan offers only {@code 350} request a day.
 * <p>
 * The class is supposed to be thread-safe.
 * <p>
 * Created by @ssz on 23.05.2021.
 */
@Component
public class CountHolder {
    private final static Logger LOGGER = LoggerFactory.getLogger(CountHolder.class);

    private final Path file;
    private final AtomicReference<CountData> data;

    public CountHolder(@Value("${app.speaker.metrics.voice-rss-counter:voice-rss.count}") String file) throws Exception {
        this.file = toAbsolutePath(file);
        this.data = new AtomicReference<>(CountData.read(this.file));
    }

    @PreDestroy
    public void save() throws IOException {
        CountData.write(file, data.get());
    }

    public CountData getCount() {
        return data.get();
    }

    public CountData increment() {
        return data.updateAndGet(CountData::increment);
    }

    /**
     * Returns an absolute path of serialization file.
     *
     * @param file {@code String} a file from settings not {@code null}
     * @return {@link Path}
     */
    public static Path toAbsolutePath(String file) {
        if (!Objects.requireNonNull(file).startsWith("/")) {
            file = "/" + file;
        }
        return Paths.get(System.getProperty("user.dir") + file).toAbsolutePath();
    }

    /**
     * A state data that is stored in the file between runs.
     */
    public static class CountData implements Serializable {
        /**
         * Timeout after which the fields {@link #current} and {@link #timestamp} are reset.
         */
        private final static long CURRENT_COUNT_PERIOD_IN_SECONDS = 24 * 60 * 60;

        private static final long serialVersionUID = 42L;

        private final long total;
        private final long current;
        private final LocalDateTime timestamp;

        protected CountData(LocalDateTime timestamp, long total, long current) {
            this.current = current;
            this.total = total;
            this.timestamp = timestamp;
        }

        public static CountData read(Path file) throws IOException, ClassNotFoundException {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Read data from file {}", file);
            }
            if (!Files.exists(file)) {
                return new CountData(LocalDateTime.now(), 0, 0);
            }
            try (InputStream is = Files.newInputStream(file); ObjectInputStream ois = new ObjectInputStream(is)) {
                return (CountData) ois.readObject();
            }
        }

        public static void write(Path file, CountData data) throws IOException {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Write {} to file {}", data, file);
            }
            try (OutputStream os = Files.newOutputStream(file); ObjectOutputStream oos = new ObjectOutputStream(os)) {
                oos.writeObject(data);
            }
        }

        public CountData set(long count) {
            return increment(count - this.current);
        }

        public CountData increment() {
            return increment(1);
        }

        public CountData increment(long add) {
            LocalDateTime now = LocalDateTime.now();
            if (Duration.between(this.timestamp, now).getSeconds() >= CURRENT_COUNT_PERIOD_IN_SECONDS) {
                return new CountData(now, this.total + add, add);
            }
            return new CountData(this.timestamp, this.total + add, this.current + add);
        }

        public long current() {
            return current;
        }

        public long total() {
            return total;
        }

        @Override
        public String toString() {
            return String.format("StateData{current=%d, total=%d, timestamp=%s}", current, total, timestamp);
        }
    }
}
