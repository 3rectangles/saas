package com.barraiser.common.monitoring;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.PrintWriter;
import java.io.StringWriter;

@Log4j2
@Aspect
@Configuration
@Profile("local")
@AllArgsConstructor
public class MonitoringAspect {
    public static final String MONITORING_SLACK_CHANNEL = "#monitoring";
    private final MonitoringSlackChannelService monitoringSlackChannelService;

    @SneakyThrows
    @Around("@annotation(com.barraiser.common.monitoring.Monitored)")
    public Object monitor(final ProceedingJoinPoint joinPoint) {
        try {
            return joinPoint.proceed();
        } catch (final Throwable throwable) {
            log.error(throwable);
            final StringWriter sw = new StringWriter();
            final PrintWriter pw = new PrintWriter(sw);
            throwable.printStackTrace(pw);

            final String name =
                    ((MethodSignature) joinPoint.getSignature())
                            .getMethod()
                            .getAnnotation(Monitored.class)
                            .message();

            this.monitoringSlackChannelService.sendMessage(
                    String.format("%s : %s", name, throwable.getMessage()),
                    sw.toString(),
                    MONITORING_SLACK_CHANNEL);
            throw throwable;
        }
    }
}
