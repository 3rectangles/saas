package com.barraiser.onboarding.common;

import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@Component
@AllArgsConstructor
public class ClassPathResourceReader {
    private final ResourceLoader resourceLoader;

    public String read(final String fileName) throws IOException {
        final Resource graphQLSchemaFile = this.resourceLoader.getResource("classpath:" + fileName);
        final Scanner scanner = new Scanner(graphQLSchemaFile.getInputStream(), StandardCharsets.UTF_8.name());
        return scanner.useDelimiter("\\A").next();
    }
}
