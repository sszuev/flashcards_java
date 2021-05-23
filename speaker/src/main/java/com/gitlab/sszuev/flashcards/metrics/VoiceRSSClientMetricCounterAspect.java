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
    private final CountHolder holder;

    public VoiceRSSClientMetricCounterAspect(MeterRegistry registry, CountHolder holder) {
        this.holder = Objects.requireNonNull(holder);
        this.counter = createCounter(registry, holder);
    }

    @AfterReturning("execution(* com.gitlab.sszuev.flashcards.service.VoiceRSSClientTTSImpl.getResource(String))")
    public void countGetResource(JoinPoint point) {
        Object res = point.getArgs()[0];
        long count = incrementAndGet();
        LOGGER.info("{}::::request='{}'", count, res);
    }

    private static Counter createCounter(MeterRegistry registry, CountHolder holder) {
        Counter res = Objects.requireNonNull(registry).counter(VOICE_RSS_TTS_NAME);
        res.increment(holder.getCount());
        return res;
    }

    private long incrementAndGet() {
        counter.increment();
        long res = (long) counter.count();
        holder.setCount(res);
        return res;
    }
}
