package com.barraiser.common.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.Instant;

@Log4j2
@Aspect
@Configuration
@AllArgsConstructor
public class ProfiledAspect {
    public static final String RESULT_TAG = "result";
    public static final String TYPE_TAG = "type";
    public static final String TIMER = "timer";
    public static final String COUNTER = "counter";
    private final MeterRegistry meterRegistry;

    @SneakyThrows
    @Around("@annotation(com.barraiser.common.monitoring.Profiled)")
    public Object profile(final ProceedingJoinPoint joinPoint) {
        final Instant start = Instant.now();
        final String name =
                ((MethodSignature) joinPoint.getSignature())
                        .getMethod()
                        .getAnnotation(Profiled.class)
                        .name();
        count(name);
        try {
            final Object result = joinPoint.proceed();
            countSuccess(name);
            return result;
        } catch (final Throwable throwable) {
            countFailure(name);
            throw throwable;
        } finally {
            recordTime(name, start, Instant.now());
        }
    }

    private void count(final String name) {
        final Counter counter = this.meterRegistry.counter(name);
        counter.count();
    }

    private void countSuccess(final String name) {
        final String[] tags = new String[] {RESULT_TAG, "success", TYPE_TAG, COUNTER};
        this.meterRegistry.counter(name, tags).count();
    }

    private void countFailure(final String name) {
        final String[] tags = new String[] {RESULT_TAG, "error", TYPE_TAG, COUNTER};
        final Counter counter = this.meterRegistry.counter(name, tags);
        counter.count();
    }

    private void recordTime(final String name, final Instant start, final Instant end) {
        final Duration timeElapsed = Duration.between(start, end);
        final String[] tags = new String[] {TYPE_TAG, TIMER};
        final Timer timer = this.meterRegistry.timer(name, tags);
        timer.record(timeElapsed);
    }
}
