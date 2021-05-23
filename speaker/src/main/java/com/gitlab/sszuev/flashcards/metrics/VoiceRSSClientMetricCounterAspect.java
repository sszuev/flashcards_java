package com.gitlab.sszuev.flashcards.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Created by @ssz on 23.05.2021.
 */
@Aspect
@Component
public class VoiceRSSClientMetricCounterAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(VoiceRSSClientMetricCounterAspect.class);

    // http://localhost:8080/actuator/metrics/voice-rss
    private static final String VOICE_RSS_TTS_NAME = "voice-rss";

    private final Counter counter;

    public VoiceRSSClientMetricCounterAspect(MeterRegistry registry) {
        Objects.requireNonNull(registry);
        this.counter = registry.counter(VOICE_RSS_TTS_NAME);
    }

    @AfterReturning("execution(* com.gitlab.sszuev.flashcards.service.VoiceRSSClientTTSImpl.getResource(String))")
    public void countGetResource(JoinPoint point) {
        Object res = point.getArgs()[0];
        counter.increment();
        LOGGER.info("{}::::request='{}'", (long) counter.count(), res);
    }
}
