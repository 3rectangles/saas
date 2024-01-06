package com.barraiser.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Media {
    private String id;

    private String category;

    private String format;

    private String internalType;

    private String context;

    private String entityType;

    private String uri;
}
