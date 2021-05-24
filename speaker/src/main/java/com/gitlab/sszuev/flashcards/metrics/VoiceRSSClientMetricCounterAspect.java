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

    // http://localhost:8080/actuator/metrics/voice-rss.current
    // http://localhost:8080/actuator/metrics/voice-rss.total
    private static final String VOICE_RSS_TTS_PREFIX = "voice-rss.";

    private final Counter currentMeterCounter;
    private final Counter totalMeterCounter;
    private final CountHolder systemCounter;

    public VoiceRSSClientMetricCounterAspect(MeterRegistry registry, CountHolder counter) {
        Objects.requireNonNull(registry);
        this.systemCounter = Objects.requireNonNull(counter);
        this.currentMeterCounter = Counter.builder(VOICE_RSS_TTS_PREFIX + "current")
                .description("The number of calls per day to the voice-rss service.")
                .register(registry);
        this.totalMeterCounter = Counter.builder(VOICE_RSS_TTS_PREFIX + "total")
                .description("The total number of calls to the voice-rss service since the beginning of observations.")
                .register(registry);
        CountHolder.CountData cdata = counter.getCount();
        this.currentMeterCounter.increment(cdata.current());
        this.totalMeterCounter.increment(cdata.total());
    }

    @AfterReturning("execution(* com.gitlab.sszuev.flashcards.service.VoiceRSSClientTTSImpl.getResource(String))")
    public void countGetResource(JoinPoint point) {
        Object res = point.getArgs()[0];
        long count = incrementAndGet();
        LOGGER.info("{}::::request='{}'", count, res);
    }

    private long incrementAndGet() {
        totalMeterCounter.increment();
        long res = systemCounter.increment().current();
        currentMeterCounter.increment(res - currentMeterCounter.count());
        return res;
    }

}
